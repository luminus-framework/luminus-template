(ns {{name}}.game
  (:require [{{name}}.tetris :as tetris]))

(defn log [& items]
  (.log js/console (apply str (interpose ", " items))))

;;Controls
(defn ^:export keyDown [evt]
  (condp = evt/keyCode
    37 (swap! tetris/OFFSET #(map + [-1 0] %)) 
    39 (swap! tetris/OFFSET #(map + [1 0] %))
    38 (reset! tetris/ROTATION :left)
    40 (reset! tetris/ROTATION :right))
  #_(log (str "tetris/OFFSET: " @tetris/OFFSET "\ntetris/ROTATION: " @tetris/ROTATION)))

;;UI
(defn clear [ctx]
  (set! (.-fillStyle ctx) "black")
  (.fillRect ctx 0 0 @tetris/WIDTH @tetris/HEIGHT))

(defn draw-square [ctx color x y]  
  (let [width  (/ @tetris/WIDTH tetris/COLS)
        height (/ @tetris/HEIGHT tetris/ROWS)
        xpos   (* x width)
        ypos   (* y width)]
        
    (set! (.-fillStyle ctx) color)    
    (.fillRect ctx xpos ypos width height)
    (set! (.-fillStyle ctx) "black")
    (.strokeRect ctx xpos ypos width height)))

(defn draw-text [ctx color text x y]
  (set! (.-fillStyle ctx) color)
  (set! (.-font ctx) "20px Verdana")
  (.fillText ctx text x y))

(defn draw-game-over [ctx score]
    (set! (.-fillStyle ctx) "rgba(255, 255, 255, 0.5)")
    (.fillRect ctx 0 0 @tetris/WIDTH @tetris/HEIGHT)
    (draw-text ctx "red" "GAME OVER" (- (/ @tetris/WIDTH 2) 50) (/ @tetris/HEIGHT 2))
    (draw-text ctx "red" (str "Final Score: " score) (- (/ @tetris/WIDTH 2) 55) (+ 15 (/ @tetris/HEIGHT 2))))


(defn draw-board [ctx board block score]     
  (clear ctx)
  
  ;render the board
  (doseq [square (range (count board))]
    (let [[x y] (tetris/pos-to-xy square)]
      (draw-square ctx (get board square) x y)))
  
  ;draw the current block
  (doseq [[x y] (:shape block)]
    (draw-square ctx (:color block) x y))
  
  (draw-text ctx "green" (str "score:" score)  20 25))

(declare game-loop)
(defn game-loop [ctx score board block old-time]       
  (reset! tetris/OFFSET [0 0])
  (reset! tetris/ROTATION nil)
  
  (draw-board ctx board block score)
  
  (let [cur-time (.getTime (new js/Date))
        new-time (if (> (- cur-time old-time) 250)
                   cur-time
                   old-time)
        drop? (> new-time old-time)
        [num-removed new-board] (tetris/clear-lines board)]
    
    (cond
      (tetris/game-over? board)
      (draw-game-over ctx score)
      
      (tetris/collides? board (:shape block))
      (js/setTimeout 
        (fn []  
          (game-loop ctx
                     score 
                     (tetris/update-board board block) 
                     (tetris/get-block) 
                     new-time))
        5)
            
      :default
      (js/setTimeout 
         (fn [] 
           (game-loop ctx
                      (+ score (* num-removed num-removed)) 
                      new-board 
                      (tetris/transform board block drop?) 
                      new-time))
         5))))

(defn ^:export startGame []    
  (let [canvas (.getElementById js/document "canvas")
        ctx (.getContext canvas "2d")]
    
    (reset! tetris/WIDTH (.-width canvas))
    (reset! tetris/HEIGHT (.-height canvas))
    
    (.addEventListener js/window "keydown" keyDown true)      
    (game-loop ctx 0 (tetris/get-board) (tetris/get-block) (.getTime (new js/Date)))))

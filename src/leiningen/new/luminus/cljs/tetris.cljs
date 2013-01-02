(ns {{name}}.tetris)

(def WIDTH (atom nil))
(def HEIGHT (atom nil))

(def COLS 10)
(def ROWS 20)

(def OFFSET (atom [0 0]))
(def ROTATION (atom nil))
(def COLORS ["red" "blue" "green" "yellow" "orange" "pink"])
(def SHAPES [[[0 1] [0 2] [0 3] [0 4]]
             [[0 0] [0 1] [1 1] [1 2]]
             [[1 2] [1 1] [0 1] [0 0]]
             [[0 1] [1 1] [1 0] [2 1]]
             [[0 0] [0 1] [1 0] [1 1]]
             [[0 0] [0 1] [0 2] [1 2]]
             [[1 0] [1 1] [1 2] [0 2]]])

(defn get-block []
  (let [shape (rand-nth SHAPES)
        offset (inc (rand-int (- COLS 3)))]
    {:color (rand-nth COLORS)
     :shape (map (fn [[x y]] [(+ x offset) y]) shape)}))

(defn get-board []
  (vec (take (* ROWS COLS) (repeat "black"))))

(defn pos-to-xy [pos]
  (let [x (mod pos COLS)
        y (int (/ (- pos x) COLS))]
    [x, y]))


(defn collides?
  ([board x y pos]
    (let [[posx posy] (pos-to-xy pos)]
      (and
        (> x (- 1))
        (< x COLS)
        (< y ROWS)
        (not (and
               (= posx x)
               (= posy y)
               (not= "black" (get board (+ pos COLS))))))))
  ([board shape pos]
    (every?
      #{true}
      (for [[x y] shape]
        (collides? board x y pos))))
  ([board shape]
    (not (reduce
           #(and %1 (collides? board shape %2))
           (range (count board)))) ))

(defn rotate [board shape]
  (if (nil? @ROTATION)
    shape
    (let [[avg-x avg-y] (->> shape
                          (reduce
                            (fn [[tx ty] [x y]]
                              [(+ tx x) (+ ty y)]))
                          (map #(int (/ % 4))))
          
          rotated (map (fn [[x y]]
                         [(int (+ avg-x (- y avg-y)))
                          (int (- avg-y (- x avg-x)))])
                       shape)]
      (if (collides? board rotated)
        shape rotated))))

(defn shift [board shape]
  (let [shifted (map
                  (fn [[x y]]
                    [(+ x (first @OFFSET)) y])
                  shape)]
    (if (collides? board shifted)
      shape shifted)))

(defn transform [board {:keys [color shape]} drop?]
  (let [rotated (->> shape (shift board) (rotate board))]
    {:color color
     :shape (if drop?
              (map (fn [[x y]] [x (if drop? (inc y) y)])
                   rotated)
              rotated)}))

(defn clear-lines [board]
  (let [new-board (->> board
                    (partition COLS)
                    (filter #(some #{"black"} %))
                    (apply concat))
        num-removed (- (count board) (count new-board))]
    [num-removed
     (into (vec (take num-removed (repeat "black")))
       new-board)]))

(defn update-board [board {:keys [color shape]}]
  (vec (map #(let [[x y] (pos-to-xy %)]
               (if (some (fn [[px py]] (and (= x px) (= y py)))
                     shape)
                 color (get board %)))
           (range (count board)))))

(defn game-over? [board]
  (not (reduce #(and %1 (= "black" %2))
         (butlast (rest (take COLS board))))))

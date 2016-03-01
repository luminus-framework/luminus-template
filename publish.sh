#!/bin/bash
VERSION=`head -1 project.clj | rev | cut -d" " -f1`
sed "s/<luminus-version>/$VERSION/g" resources/leiningen/new/luminus/core/README.md
echo published Luminus $VERSION
git commit -a -m "relese $VERSION" && git push
echo published Luminus $VERSION

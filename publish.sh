#!/bin/bash
README="resources/leiningen/new/luminus/core/README.md"
VERSION=`head -1 project.clj | rev | cut -d" " -f1`
sed "s/<luminus-version>/$VERSION/g" "$README".in > $README
git commit -a -m "relese $VERSION" && git push
echo published Luminus $VERSION

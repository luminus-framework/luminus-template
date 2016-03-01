#!/bin/bash
README="resources/leiningen/new/luminus/core/README.md"
VERSION=`head -1 project.clj | rev | cut -d" " -f1 | rev`
sed "s/<luminus-version>/$VERSION/g" "$README".in > $README
git commit -a -m "release $VERSION" && git push
echo published Luminus $VERSION

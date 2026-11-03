#!/bin/sh
URL=http://archive.ubuntu.com/ubuntu

demo() {
    echo "\$ ./run.sh $*"
    ./run.sh "$@" 2>&1
    echo "[код завершения: $?]"
    echo
}

demo -p curl -r "$URL"
demo -p A -r tests/data/test_repo.txt -t -o out.svg -a
demo -r "$URL"
demo -p 'Curl!' -r "$URL"
demo -p curl -r tests/data/test_repo.txt -t
demo -p curl
demo -p curl -r ftp://example.com
demo -p curl -r tests/data/test_repo.txt
demo -p A -r tests/data/missing.txt -t
demo -p curl -r "$URL" --test-mode=maybe
demo -p curl -r "$URL" -o graph.png
demo -p curl -r "$URL" -o no/such/dir/graph.svg
demo -p curl -r "$URL" --ascii-tree=maybe
demo -p
demo -p curl -p wget -r "$URL"
demo --unknown
demo extra
demo -p 'Bad!' -r bad -o bad.png -a=bad

.PHONY: build test demo clean

build:
	mkdir -p out/main
	javac --release 17 -encoding UTF-8 -d out/main src/depviz/*.java

test: build
	mkdir -p out/test
	javac --release 17 -encoding UTF-8 -cp out/main -d out/test tests/depviz/*.java
	java -cp out/main:out/test depviz.MainTest

demo:
	sh tests/demo_errors.sh

clean:
	rm -rf out

JC=javac
all:create_dir
	$(JC) -d out/ Main.java
create_dir:
	mkdir -p out
clean:
	rm -rf out
.PHONY: clean
server: server.hs
	ghc --make server.hs

a.out: server.c
	clang server.c

clean:
	rm -f a.out server server.hi 

.PHONY: clean all

all: server a.out pong-udp
server: server.hs
	ghc --make server.hs

a.out: server.c
	clang server.c

clean:
	rm -f a.out server pong-udp.hi pong-udp server.hi

pong-udp: pong-udp.hs
	ghc --make pong-udp.hs

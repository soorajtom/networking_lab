#!/bin/sh
# Copyright 2014 Vivien Didelot <vivien@didelot.org>
# Licensed under the terms of the GNU GPL v3, or any later version.

NICK=stom
SERVER=irc.freenode.net
PORT=6667
CHAN="##iitpkd-networking"

{
  # join channel and say hi
  cat << IRC
NICK stom1115010
USER irccat 8 x : irccat
JOIN ##iitpkd-networking
PRIVMSG ##iitpkd-networking :Im in
IRC

  # forward messages from STDIN to the chan, indefinitely
  while read line ; do
    echo "$line" | sed "s/^/PRIVMSG $CHAN :/"
  done

  # close connection
  echo QUIT
} | nc $SERVER $PORT | while read line ; do
  case "$line" in
    *PRIVMSG\ $CHAN\ :*) echo "$line" | cut -d: -f3- ;;
    #*) echo "[IGNORE] $line" >&2 ;;
  esac
done

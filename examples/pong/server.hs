{-# LANGUAGE OverloadedStrings #-}
import Network.Socket hiding (send)
import Network.Socket.ByteString

main :: IO ()
main = do
  sock <- socket AF_INET Stream 0
  setSocketOption sock ReuseAddr 1
  bind sock (SockAddrInet 8001 iNADDR_ANY)
  listen sock 2
  mainLoop sock

mainLoop :: Socket -> IO ()
mainLoop sock = do
  conn <- accept sock
  worker conn
  mainLoop sock

worker :: (Socket, SockAddr) -> IO ()
worker (sock,address) = do putStrLn $ unwords ["from", show address]
                           send sock "pong"
                           shutdown sock ShutdownBoth


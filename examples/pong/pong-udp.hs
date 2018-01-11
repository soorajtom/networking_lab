{-# LANGUAGE OverloadedStrings #-}
import Data.ByteString.Char8  as BS
import Network.Socket      hiding (send, sendTo, recvFrom)
import Network.Socket.ByteString


main :: IO ()
main = do
  sock <- socket AF_INET Datagram 0
  setSocketOption sock ReuseAddr 1
  bind sock (SockAddrInet 8001 iNADDR_ANY)
  mainLoop sock

mainLoop :: Socket -> IO ()
mainLoop sock = do
  (msg,address) <- recvFrom sock 4
  BS.putStrLn $ BS.unwords [BS.pack $ show address, "<-", msg]
  sendTo sock "pong" address
  mainLoop sock


-- This is a minimal re-implementation of the famous netcat client in
-- Haskell.
import           Control.Concurrent ( forkIO, yield )
import           Control.Monad ( when          )
import           Data.Maybe
import           Network.Socket as Socket
import qualified Data.ByteString           as BS
import qualified Network.Socket.ByteString as BSocket
import           System.Environment ( getArgs     )
import           System.Exit        ( exitFailure, exitSuccess )
import           System.IO



-- The main nc loop.
nc :: (Family, SockAddr) -> IO ()
nc (family, sockAddr) = do
  sock     <- socket family Stream 0
  connect sock sockAddr
  forkIO $ sendIt sock >> shutdown sock ShutdownSend
  receiveIt sock
  shutdown sock ShutdownBoth


main :: IO ()
main = do args <- getArgs
          case args of
            [host, service] -> addressLookup host service >>= nc
            _               -> err "error: nc host port"

sendIt :: Socket -> IO ()
sendIt sock = do
  cond     <- hIsEOF stdin
  when (not cond) $ do
    someData <- BS.hGetSome stdin 1024
    sockEOI <- isWritable sock
    when sockEOI $ do BSocket.sendAll sock someData
                      sendIt sock


receiveIt :: Socket -> IO ()
receiveIt sock = do
  sockReadble <- isReadable sock
  when sockReadble $ do
    BSocket.recv sock 1024 >>= BS.putStr
    receiveIt sock


-- Looking up address and service.
addressLookup :: String -> String -> IO (Family, SockAddr)
addressLookup host port = do
  adrs <- listToMaybe <$> getAddrInfo (Just defaultHints) (Just host)  (Just port)
  maybe errMesg result adrs
  where errMesg = err $ "nc: cannot resolve host=" ++ host
                  ++ " service=" ++ port
        result a = return (addrFamily a, addrAddress a)


err :: String -> IO a
err msg = hPutStrLn stderr msg >> exitFailure

{-# LANGUAGE OverloadedStrings #-}

import Control.Concurrent      ( forkIO, threadDelay      )
import Control.Exception       ( catch, throwIO           )
import Control.Monad           ( void                     )
import Data.Word               ( Word8                    )
import Foreign.Marshal.Alloc   ( allocaBytes              )
import Foreign.Ptr             ( Ptr                      )
import System.Environment      ( getArgs                  )
import System.IO
import System.IO.Error         ( isEOFError               )
import System.CPUTime          ( getCPUTime               )
import Network.Socket


bufSize :: Int
bufSize = 1024


main :: IO ()
main = do args <- getArgs
          case args of
            [p,d] -> program (read p) (Just $ read d)
            [p]   -> program (read p) Nothing
            []    -> program 8000 Nothing
            _     -> do hPutStrLn stderr $ unlines [ "Usage: blackhole port rate"]

program :: PortNumber -> Maybe Integer -> IO ()
program port rate = do
  sock <- socket AF_INET Stream 0
  setSocketOption sock ReuseAddr 1
  bind sock (SockAddrInet port iNADDR_ANY)
  listen sock 2
  mainLoop sock rate

mainLoop :: Socket -> Maybe Integer -> IO ()
mainLoop sock rate = do
  conn  <- accept sock
  void $ forkIO $ allocaBytes bufSize (worker conn rate)
  mainLoop sock rate


myRecv :: Socket -> Ptr Word8 -> IO Int
myRecv sock ptr = catch (recvBuf sock ptr bufSize)
  (\ e -> if isEOFError e then return 0 else throwIO e)


worker :: (Socket, SockAddr) -> Maybe Integer -> Ptr Word8 -> IO ()
worker (sock,address) mrate ptr = do
  putStrLn $ "*" ++ show address
  total <- go 0
  shutdown sock ShutdownBoth
  putStrLn $ show address ++ "--{" ++ show total ++ " bytes} --> blackhole."
  where go sofar = do (count,delay) <- readAndTime
                      threadDelay $ delayTime delay
                      if count > 0 then go (sofar + count) else return sofar
        delayTime rtm | wtTm <= 0 = 0
                      | otherwise = fromEnum wtTm
          where rtmMSec         = rtm `quot` 1000000
                k1ReadMSec rate = (toEnum bufSize * 1000000) `quot` rate
                wtTm        = maybe 0 (\ r -> k1ReadMSec r - rtmMSec) mrate
        readAndTime = do x  <- getCPUTime
                         count <- myRecv sock ptr
                         y <- getCPUTime
                         return (count, y - x)

/** =============================
## Handling endian in ios
============================== */

import Foundation
import CoreFoundation

let integer = 32

// MARK: Converts a 16-bit integer from big-endian format to the host’s native byte order.
CFSwapInt32BigToHost(arg: UInt32(bigEndian: integer))

// MARK: Converts a 16-bit integer from the host’s native byte order to big-endian format.
CFSwapInt32HostToBig(arg: UInt32(integer))

// MARK: Converts a 16-bit integer from little-endian format to the host’s native byte order.
CFSwapInt16LittleToHost(arg: UInt32(littleEndian: integer))

// MARK: Converts a 16-bit integer from the host’s native byte order to littile-endian format.
CFSwapInt32HostToLittle(arg: UInt32(integer))

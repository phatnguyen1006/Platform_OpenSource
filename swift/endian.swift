/** =============================
## Handling endian in ios
============================== */

import Foundation
import CoreFoundation

let integer = 32
CFSwapInt32BigToHost(arg: UInt32(bigEndian: integer))
CFSwapInt32HostToBig(arg: UInt32(integer))
CFSwapInt32HostToLittle(arg: UInt32(littleEndian: integer))
CFSwapInt32HostToLittle(arg: UInt32(integer))

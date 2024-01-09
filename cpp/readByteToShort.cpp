//
// Created by nhuulong1 on 09/01/2024.
//

#include "my_exts.h"
#include <memory>

int16_t my_exts::readByteToShort(char* bytes, bool isLittleEndian) {
    if (isLittleEndian)
        return (int16_t)bytes[1] << 8 | bytes[0];
    return *(int16_t *)((void *)bytes);
}

int16_t *my_exts::readBytesToShorts(char *bytes, size_t arrSize, bool isLittleEndian) {
    int16_t* retVal = new int16_t[arrSize / 2];

    for (int i = 0; i < arrSize / 2; i++) {
        retVal[i] = readByteToShort(&bytes[i * 2], isLittleEndian);
    }

    return retVal;
}

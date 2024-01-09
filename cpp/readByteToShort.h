
#ifndef SIMPLE_AUDIO_PLAYER_MY_EXTS_H
#define SIMPLE_AUDIO_PLAYER_MY_EXTS_H

#include <cstdint>

class my_exts {
private:
    static int16_t readByteToShort(char* bytes, bool isLittleEndian);
public:
    static int16_t* readBytesToShorts(char* bytes, size_t arrSize, bool isLittleEndian);
};


#endif //SIMPLE_AUDIO_PLAYER_MY_EXTS_H

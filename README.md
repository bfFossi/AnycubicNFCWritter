## This app can be used to create NFC tags for automatic filament recognition in an ACE-Pro from Anycubic.

![App-Image](/anycubicnfcwriter.jpg "This is a appimage.")

### The realisation is based on: 
https://www.reddit.com/r/anycubic/comments/1g047ad/ace_pro_nfc_tag_research_create_own_tags/

**data structure of NFC Tag Memory:**


| Data                          | Value | Description |
|--------------------------------|:-------------:|:-------------:|
|[ 1D:76:39:DA ] Sektor 00 : DATA|
|[ 3D:98:00:00 ] Sektor 01 : DATA
|[ A5:A3:00:00 ] Sektor 02 : DATA
|[ E1:10:12:00 ] Sektor 03 : DATA
|[ 7B:00:65:00 ] Sektor 04 : DATA
|[ 41:48:48:53 ] Sektor 05 : DATA|AHHS|product description
|[ 42:52:2D:31 ] Sektor 06 : DATA|BR-1|product description
|[ 30:32:00:00 ] Sektor 07 : DATA|02|product description
|[ 00:00:00:00 ] Sektor 08 : DATA
|[ 00:00:00:00 ] Sektor 09 : DATA
|[ 41:43:00:00 ] Sektor 0A : DATA|AC|brand (AC=AnyCubic)
|[ 00:00:00:00 ] Sektor 0B : DATA||brand
|[ 00:00:00:00 ] Sektor 0C : DATA||brand
|[ 00:00:00:00 ] Sektor 0D : DATA
|[ 00:00:00:00 ] Sektor 0E : DATA
|[ 50:4C:41:3F ] Sektor 0F : DATA|PLA?|type
|[ 48:69:67:68 ] Sektor 10 : DATA|High|type
|[ 3F:53:70:65 ] Sektor 11 : DATA|?Spe|type
|[ 65:64:00:00 ] Sektor 12 : DATA|ed|type
|[ 00:00:00:00 ] Sektor 13 : DATA
|[ FF:00:06:E1 ] Sektor 14 : DATA
|[ 00:00:00:00 ] Sektor 15 : DATA
|[ 00:00:00:00 ] Sektor 16 : DATA
|[ 32:00:96:00 ] Sektor 17 : DATA|50-150|printing speed 1 in mm/s
|[ BE:00:D2:00 ] Sektor 18 : DATA|190-210|temperature range for printing speed 1 in degree
|[ 96:00:2C:01 ] Sektor 19 : DATA|150-300|printing speed 2 (300d=0x012C) in mm/s, optional else 00:00:00:00
|[ D2:00:E6:00 ] Sektor 1A : DATA|210-230|temperature range in degree for printing speed 2, optional else 00:00:00:00
|[ 2C:01:58:02 ] Sektor 1B : DATA|300-600|printing speed 3 (600d=0x0258) in mms/s, optional else 00:00:00:00
|[ E6:00:04:01 ] Sektor 1C : DATA|230-260|temperature range in degree for printing speed 3, optional else 00:00:00:00
|[ 32:00:3C:00 ] Sektor 1D : DATA|50-60|bed temperature in degree
|[ AF:00:4A:01 ] Sektor 1E : DATA|| Diameter, (4A:01) = 1,75mm, (AF:00) = unknown (maybe length of Filament in m)
|[ E8:03:00:00 ] Sektor 1F : DATA
|[ 00:00:00:00 ] Sektor 20 : DATA
|[ 00:00:00:00 ] Sektor 21 : DATA
|[ 00:00:00:00 ] Sektor 22 : DATA
|[ 00:00:00:00 ] Sektor 23 : DATA
|[ 00:00:00:00 ] Sektor 24 : DATA
|[ 00:00:00:00 ] Sektor 25 : DATA
|[ 00:00:00:00 ] Sektor 26 : DATA
|[ 00:00:00:00 ] Sektor 27 : DATA
|[ 00:00:00:BD ] Sektor 28 : DATA
|[ 04:00:00:04 ] Sektor 29 : DATA
|[ 47:00:00:00 ] Sektor 2A : DATA
|[ 00:00:00:00 ] Sektor 2B : DATA
|[ 00:00:00:00 ] Sektor 2C : DATA

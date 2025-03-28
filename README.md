This app can be used to create NFC tags for automatic filament recognition in an ACE-Pro from Anycubic.

The realisation is based on:
https://www.reddit.com/r/anycubic/comments/1g047ad/ace_pro_nfc_tag_research_create_own_tags/

data structure of NFC Tag Memory:

[ 1D:76:39:DA ] Sektor 00 : DATA<br/>
[ 3D:98:00:00 ] Sektor 01 : DATA<br/>
[ A5:A3:00:00 ] Sektor 02 : DATA<br/>
[ E1:10:12:00 ] Sektor 03 : DATA<br/>
[ 7B:00:65:00 ] Sektor 04 : DATA<br/>
[ 41:48:48:53 ] Sektor 05 : DATA &emsp;-> AHHS &emsp;&emsp;-> product description<br/>
[ 42:52:2D:31 ] Sektor 06 : DATA &emsp;-> BR-1 &emsp;&emsp;-> product description<br/>
[ 30:32:00:00 ] Sektor 07 : DATA &emsp;-> 02&nbsp;&nbsp;&emsp;&emsp;-> product description<br/>
[ 00:00:00:00 ] Sektor 08 : DATA<br/>
[ 00:00:00:00 ] Sektor 09 : DATA<br/>
[ 41:43:00:00 ] Sektor 0A : DATA &emsp;-> AC &emsp;&emsp;      -> brand<br/>
[ 00:00:00:00 ] Sektor 0B : DATA &emsp;-> &nbsp;&nbsp; &emsp;&emsp;brand<br/>
[ 00:00:00:00 ] Sektor 0C : DATA &nbsp;&nbsp;&nbsp;&nbsp;&emsp; &emsp; &emsp;&emsp;-> brand<br/>
[ 00:00:00:00 ] Sektor 0D : DATA<br/>
[ 00:00:00:00 ] Sektor 0E : DATA<br/>
[ 50:4C:41:3F ] Sektor 0F : DATA &emsp; -> PLA? &emsp; &emsp; -> type<br/>
[ 48:69:67:68 ] Sektor 10 : DATA &emsp; -> High &emsp; &emsp;   -> type<br/>
[ 3F:53:70:65 ] Sektor 11 : DATA &emsp; -> ?Spe &emsp; &emsp;   -> type<br/>
[ 65:64:00:00 ] Sektor 12 : DATA &emsp; -> ed   &emsp; &emsp;   -> type<br/>
[ 00:00:00:00 ] Sektor 13 : DATA<br/>
[ FF:00:06:E1 ] Sektor 14 : DATA<br/>
[ 00:00:00:00 ] Sektor 15 : DATA<br/>
[ 00:00:00:00 ] Sektor 16 : DATA<br/>
[ 32:00:96:00 ] Sektor 17 : DATA &emsp; -> 50-150 mm &emsp;&emsp;&emsp;									-> printing speed 1<br/>
[ BE:00:D2:00 ] Sektor 18 : DATA &emsp; -> 190-210 degree &emsp;&emsp;&emsp;            -> temperature range for printing speed 1<br/>
[ 96:00:2C:01 ] Sektor 19 : DATA &emsp; -> 150-300mm (300d=0x012C) &emsp;&emsp;&emsp;   -> printing speed 2, optional if available (else 00:00:00:00)<br/>
[ D2:00:E6:00 ] Sektor 1A : DATA &emsp; -> 210-230 degree &emsp;&emsp;&emsp;            -> temperature range for printing speed 2 optional if available<br/>
[ 2C:01:58:02 ] Sektor 1B : DATA &emsp; -> 300-600 mm/s (600d=0x0258) &emsp;&emsp;&emsp;-> printing speed 2 optional if available<br/>
[ E6:00:04:01 ] Sektor 1C : DATA &emsp; -> 230-260 degree &emsp;&emsp;&emsp;            -> temperature range for printing speed 3 optional if available<br/>
[ 32:00:3C:00 ] Sektor 1D : DATA &emsp; -> 50-60 degree &emsp;&emsp;&emsp;              -> bed temperature<br/>
[ AF:00:4A:01 ] Sektor 1E : DATA &emsp; -> AF:00 = Diameter 1,75 &emsp;&emsp;&emsp;     -> Diameter, 4A:01 unknown (maybe length of Filament in m)<br/>
[ E8:03:00:00 ] Sektor 1F : DATA<br/>
[ 00:00:00:00 ] Sektor 20 : DATA<br/>
[ 00:00:00:00 ] Sektor 21 : DATA<br/>
[ 00:00:00:00 ] Sektor 22 : DATA<br/>
[ 00:00:00:00 ] Sektor 23 : DATA<br/>
[ 00:00:00:00 ] Sektor 24 : DATA<br/>
[ 00:00:00:00 ] Sektor 25 : DATA<br/>
[ 00:00:00:00 ] Sektor 26 : DATA<br/>
[ 00:00:00:00 ] Sektor 27 : DATA<br/>
[ 00:00:00:BD ] Sektor 28 : DATA<br/>
[ 04:00:00:04 ] Sektor 29 : DATA<br/>
[ 47:00:00:00 ] Sektor 2A : DATA<br/>
[ 00:00:00:00 ] Sektor 2B : DATA<br/>
[ 00:00:00:00 ] Sektor 2C : DATA<br/>

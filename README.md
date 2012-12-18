CtoLLVM
=======

V terajsom stave kompilator cita subor test.c a vyraba subor test.ll. Prikladom skompilovatelneho
suboru je example.c.
Kompiluje sa prikazom:
ant run

Ten pocas behu aj vo formate .dot vypise sparsovany strom (ak ho chceme pouzit, odporucam 
kompilovat prikazom ant run -emacs). Ten sa da copy&pastnut do stranky:
http://graphviz-dev.appspot.com/

LLVM sa pusta prikazmi: llvm-as -f test.ll && lli test.bc

Co este nejde (a malo by):
- postfixove ++, -- (andras)
- switch
- enumy
- suffixy pri konstantach (andras)
- compile time vyhodnocovanie konstatnych vyrazov (andras posledne)
- kontrola hranic konstant (andras)
- inicializacia pri deklaracii

Isto nebude:
- sizeof v runtime case (v compile time uz je spraveny)
- polia, ktorych dlzka nie je cele cislo
- polia (ani v argumentoch), ktore maju prazdnu deklaraciu (akoze char x[])

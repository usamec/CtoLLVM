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

Known problems:
- compile time vyhodnocovanie konstatnych vyrazov nefunguje (teda neda spravit napr. char x[3+5];)
- sizeof v runtime case (v compile time uz je spraveny)
- polia, ktorych dlzka nie je konstanta
- polia (ani v argumentoch), ktore maju prazdnu deklaraciu (akoze char x[])
- deklaracie struktur mimo global scopu nie su povolene
- nefunguju deklaracie vnorenych funkcii
- pri zlej verzii LLVM moze blbnut ked je v jednom subore aj deklaracia aj definicia funkcie (pri
    verzii 2.9 to funguje)
- porovanie cez nerovnosti medzi pointerom a integerom je zakazane

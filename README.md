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
- &, ^, |, ==, !=, &=, ^=, |=, \<\<, \>\> (a ich verzie s =)
- for
- switch
- ternarny operator (X ? Y : Z)
- enumy
- polia, ktorych dlzka nie je cele cislo
- suffixy pri konstantach
- compile time vyhodnocovanie konstatnych vyrazov
- kontrola hranic konstant
- inicializacia pri deklaracii

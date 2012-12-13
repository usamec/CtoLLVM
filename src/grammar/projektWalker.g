tree grammar projektWalker;

options {
  tokenVocab = projekt;
  ASTLabelType=CommonTree;
}
@header {
  package ctollvm.parser;
  import ctollvm.*;
}

@members {
  Scope currentScope = new Scope();
}

walk returns [PNode node]
@init {
  ProgramNode pn = new ProgramNode();
  node = pn;
}
  : (function_definition {pn.addItem($function_definition.node);}
     | declaration {pn.addItem($declaration.node);})* 
;

function_definition returns [PNode node]
@init {
  Scope scope = new Scope(currentScope);
  currentScope = scope;
  FunctionDefinitionNode fn = new FunctionDefinitionNode(currentScope);
  node = fn;
}
@after {
  currentScope = currentScope.parent();
}
  : ^(FUNCDEF //t=Type_specifier {fn.setType($t.text);}
     (
      'typedef' {fn.setStorageSpecifier("typedef");} |
      'extern' {fn.setStorageSpecifier("extern");} |
      'static' {fn.setStorageSpecifier("static");} |
      'auto' {fn.setStorageSpecifier("auto");} |
      'register' {fn.setStorageSpecifier("register");} |
      t=Type_specifier {fn.addTypeSpecifier($t.text);} |
      i=Identifier {fn.setTypedef($i.text);} |
      s=struct_or_union_specifier {fn.setStruct($s.node);} |
      su=struct_or_union_user {fn.setStruct($su.node);}
      )*

     (fd=dec_node {fn.setFunctionDeclaration($fd.node);})
      b=block_item_list {fn.setBli($b.node);}
   )
;

compound_statement returns [PNode node]
@init {
  Scope scope = new Scope(currentScope);
  currentScope = scope;
}
@after {
  currentScope = currentScope.parent();
}
 : ^(COMPOUND b=block_item_list) {node = $b.node;}
;

parameter_declaration returns [FunctionParameterNode node]
@init {
  FunctionParameterNode fn = new FunctionParameterNode(currentScope);
  node = fn;
}
  : ^(PDEC
      (
      'typedef' {fn.setStorageSpecifier("typedef");} |
      'extern' {fn.setStorageSpecifier("extern");} |
      'static' {fn.setStorageSpecifier("static");} |
      'auto' {fn.setStorageSpecifier("auto");} |
      'register' {fn.setStorageSpecifier("register");} |
      t=Type_specifier {fn.addTypeSpecifier($t.text);} |
      i=Identifier {fn.setTypedef($i.text);} |
      s=struct_or_union_user {fn.setStruct($s.node);}
      )*
      d=declarator {fn.setDeclaration($d.node);})
;


block_item_list returns [PNode node]
@init {
  BlockItemList bn = new BlockItemList();
  node = bn;
}
  : ^(BLI (declaration {bn.addBlockItem($declaration.node);} |
           statement {bn.addBlockItem($statement.node);}
           )*)
;

statement returns [PNode node]
  : (expression {node = $expression.node;} |
     compound_statement {node = $compound_statement.node;} |
     selection_statement {node = $selection_statement.node;} |
     iteration_statement {node = $iteration_statement.node;} |
     jump_statement {node = $jump_statement.node;} |
     EMPTYSTAT {node = new EmptyStatementNode();} )
;

selection_statement returns [PNode node]
  : ^('if' e=expression s1=statement s2=statement?) {node = new IfStatementNode(
      $e.node, $s1.node, $s2.node);}
;

iteration_statement returns [PNode node]
  : ^('while' e=expression s=statement) {node = new WhileStatementNode(
      $e.node, $s.node, currentScope);} |
	^('do' s=statement e=expression) {node = new DoStatementNode(
     $e.node, $s.node, currentScope);}  
;

jump_statement returns [PNode node]
  : ^('return' e=expression?) {node = new ReturnStatementNode($e.node, currentScope);}
  | 'break' {node = new BreakStatementNode(currentScope);}
  | 'continue' {node = new ContinueStatementNode(currentScope);}
;

struct_or_union_specifier returns [StructDeclarationNode node]
@init {
  StructDeclarationNode sd = new StructDeclarationNode(currentScope);
  node = sd;
}
  : ^(STRUCTDEC 
      (id=Identifier {sd.setName($id.text);})?
      (declaration {sd.addDeclaration($declaration.node);})+
     ) 
;

struct_or_union_user returns [StructDeclarationNode node]
@init {
  StructDeclarationNode sd = new StructDeclarationNode(currentScope);
  node = sd;
}
  : ^(STRUCTUSE
      (id=Identifier {sd.setName($id.text); sd.setUse();}))
;

declaration returns [DeclarationNode node]
@init {
  DeclarationNode dn = new DeclarationNode(currentScope);
  node = dn;
}
  : ^(DEC
      (
      'typedef' {dn.setStorageSpecifier("typedef");} |
      'extern' {dn.setStorageSpecifier("extern");} |
      'static' {dn.setStorageSpecifier("static");} |
      'auto' {dn.setStorageSpecifier("auto");} |
      'register' {dn.setStorageSpecifier("register");} |
      t=Type_specifier {dn.addTypeSpecifier($t.text);} |
      i=Identifier {dn.setTypedef($i.text);} | 
      s=struct_or_union_specifier {dn.setStruct($s.node);} |
      s=struct_or_union_user {dn.setStruct($s.node);}
      )*
      (d=declarator {dn.addDeclarationProcessor($d.node);} )*)
;

declarator returns [DeclarationProcessor node]
  : ^(IDEC (
       id=Identifier {node = new DeclarationProcessor(); node.setName($id.text);} |
       dn=dec_node {node = $dn.node;})
     )
;

dec_node returns [DeclarationProcessor node]
  : (fd=function_declarator {node = $fd.node;} |
     pd=pointer_declarator {node = $pd.node;} |
     ad=array_declarator {node = $ad.node;}
    )
;

pointer_declarator returns [DeclarationProcessor node]
@init {
  PointerDeclarationProcessor pp = new PointerDeclarationProcessor();
  node = pp;
}
  : ^(POINTER (
       id=Identifier {pp.setName($id.text);} |
       dn=dec_node {pp.setChild($dn.node);}
      ))
;


function_declarator returns [DeclarationProcessor node]
@init {
  FunctionDeclarationProcessor dp = new FunctionDeclarationProcessor();
  node = dp;
}
  : ^(FUNCDEC (
       id=Identifier {dp.setName($id.text);} |
       dn=dec_node {dp.setChild($dn.node);}
      )
      (p=parameter_declaration {dp.addParameter($p.node);})*
      ('...' {dp.setVarArgs();})?)
;

array_declarator returns [DeclarationProcessor node]
@init {
  ArrayDeclarationProcessor ap = new ArrayDeclarationProcessor();
  node = ap;
}
  : ^(ARRAYDEC (
       id=Identifier {ap.setName($id.text);} |
       dn=dec_node {ap.setChild($dn.node);}
      )
      e=expression {ap.setExpression($e.node);}
    )
;

expression returns [PNode node]
  : ^('+' a=expression b=expression) {node = new AddNode($a.node, $b.node);}
  | ^('-' a=expression b=expression) {node = null;}
  | ^('*' a=expression b=expression) {node = new MulNode($a.node, $b.node);}
  | ^('/' a=expression b=expression) {node = new DivNode($a.node, $b.node);}
  | ^('%' a=expression b=expression) {node = new RemNode($a.node, $b.node);}
  | ^('=' a=expression b=expression) {node = new AssigmentNode($a.node, $b.node);}
  | ^('+=' a=expression b=expression) {node = new AssigmentOperationNode($a.node, $b.node, "+");}
  | ^('*=' a=expression b=expression) {node = null;}
  | ^('/=' a=expression b=expression) {node = null;}
  | ^('%=' a=expression b=expression) {node = null;}
  | ^('-=' a=expression b=expression) {node = null;}
  | ^('<<=' a=expression b=expression) {node = null;}
  | ^('>>=' a=expression b=expression) {node = null;}
  | ^('&=' a=expression b=expression) {node = null;}
  | ^('^=' a=expression b=expression) {node = null;}
  | ^('|=' a=expression b=expression) {node = null;}
  | ^('<' a=expression b=expression) {node = new CompareNode($a.node, $b.node, "<");}
  | ^('>' a=expression b=expression) {node = new CompareNode($a.node, $b.node, ">");}
  | ^('<=' a=expression b=expression) {node = new CompareNode($a.node, $b.node, "<=");}
  | ^('>=' a=expression b=expression) {node = new CompareNode($a.node, $b.node, ">=");}
  | ^('==' a=expression b=expression) {node = null;}
  | ^('!=' a=expression b=expression) {node = null;}
  | ^('&&' a=expression b=expression) {node = new LogicalAndNode($a.node, $b.node);}
  | ^('||' a=expression b=expression) {node = new LogicalOrNode($a.node, $b.node);}
  | ^(PREFIXPLUSPLUS a=expression) {node = new AssigmentOperationNode($a.node,
      new IntegerConstantNode("1"), "+");}
  | ^(PREFIXMINUSMINUS a=expression) {node = new AssigmentOperationNode($a.node,
      new IntegerConstantNode("-1"), "+");}
  | ^(UNARYPLUS a=expression) 
  | ^(UNARYMINUS a=expression)
  | ^(UNARYDEREFERENCE a=expression) {node = new DereferenceNode($a.node);}
  | ^(UNARYADDRESS a=expression) {node = new AddressNode($a.node);}
  | ^(ARRAYSUBS a=expression b=expression)
        {node = new DereferenceNode(new AddNode($a.node, $b.node));}
  | ^(STRUCTMEMBER a=expression i=Identifier)
        {node = new StructMemberNode($a.node, $i.text);}
  | ^(STRUCTMEMBERPOINT a=expression i=Identifier)
        {node = new StructMemberNode(new DereferenceNode($a.node), $i.text);}
  | f=function_call {node = $f.node;}
  | i=Identifier {node = new IdentifierNode($i.text, currentScope);}
  | i=Integer {node = new IntegerConstantNode($i.text);}
  | i=Float {node = new FloatingConstantNode($i.text);}
  | i=String_constant {node = new StringConstantNode($i.text);}
  | i=Character_constant {node = new CharacterConstantNode($i.text);}
;

function_call returns [PNode node]
@init {
  FunctionCallNode fc = new FunctionCallNode(currentScope);
  node = fc;
}
 : ^(FUNCCALL a=expression {fc.setFunctionName($a.node);}
     (e=expression {fc.addArgument($e.node);})*
    )
;

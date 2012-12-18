int printf(char *x, ...);

int add(int a, int b) {
  return a + b + 47;
}

int main() {
  int b[2][2];
  b[0][0] = 1l;
  b[1][0] = 42;
  int i;
  i = 0;
  while (i < 7) {
    printf("%d\n", add(b[0][0], b[1][0]));
    i = i + 1;
  }
}

from graphviz import Digraph

class Node:
    def __init__(self, value):
        self.value = value
        self.left = None
        self.right = None
aritmetica = {'+': 1, '-': 1, '*': 2, '/': 2}
def funcion(expresion):
    salida = []
    pila = []
    tokens = expresion.replace('(', ' ( ').replace(')', ' ) ').split()
    for token in tokens:
        if token.isnumeric():
            salida.append(token)
        elif token in '+-*/':
            while (pila and pila[-1] != '(' and
                   aritmetica[token] <= aritmetica[pila[-1]]):
                salida.append(pila.pop())
            pila.append(token)
        elif token == '(':
            pila.append(token)
        elif token == ')':
            while pila and pila[-1] != '(':
                salida.append(pila.pop())
            pila.pop()
    while pila:
        salida.append(pila.pop())
    return salida
def construir(postfijo):
    pila = []
    for token in postfijo:
        if token.isnumeric():
            pila.append(Node(token))
        else:
            nodo = Node(token)
            nodo.right = pila.pop()
            nodo.left = pila.pop()
            pila.append(nodo)
    return pila[0]
def dibujar(raiz):
    dot = Digraph()
    def agregar_nodo(nodo):
        if nodo:
            dot.node(str(id(nodo)), nodo.value)
            if nodo.left:
                dot.edge(str(id(nodo)), str(id(nodo.left)))
                agregar_nodo(nodo.left)
            if nodo.right:
                dot.edge(str(id(nodo)), str(id(nodo.right)))
                agregar_nodo(nodo.right)
    agregar_nodo(raiz)
    return dot
Inicio = input("Ingresar la expresion aritmetica utilizando espacios entre cada numero y simbolo:\n> ")
try:
    postfijo = funcion(Inicio)
    raiz = construir(postfijo)
    arbol = dibujar(raiz)
    archivo = arbol.render('arbol', format='png', view=True)
    print(f"Generando Arbol: {archivo}")
except Exception as e:
    print(f"Error: {e}")

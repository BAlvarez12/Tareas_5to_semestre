import generador_numeros
import buscador

file_path = "numeros.txt"

# Genera los números aleatorios
generador_numeros.generador_numeros(file_path)

while True:
    # Pide un número al usuario
    num_to_search = int(input("Ingrese el número a buscar: "))
    
    # Realiza la búsqueda
    if buscador.buscador(file_path, num_to_search):
        print(f"El número {num_to_search} se encuentra en el archivo.")
    else:
        print(f"El número {num_to_search} no se encuentra en el archivo.")
    
    # Pregunta si quiere buscar otro número
    continuar = input("buscar otro numero? (s/n): ").strip().lower()
    if continuar != 's':
        print("Saliendo del programa...")
        break

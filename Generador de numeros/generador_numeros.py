import random

def generador_numeros(file_path, count=10_000_000, min_val=-50_000_00, max_val=50_000_00):
    open(file_path, "w").close()
    chunk_size = 10_000  
    with open(file_path, "w", buffering=1024*1024) as file:  
        buffer = []
        for i in range(count):
            number = random.randint(min_val, max_val)  
            buffer.append(f"{number}\n") 

            if i % chunk_size == 0:
                file.write("".join(buffer))
                buffer = []  
        
        if buffer:
            file.write("".join(buffer))
    print(f"Se generaron {count} números aleatorios en {file_path}")

if __name__ == "__main__":
    generador_numeros("numeros.txt")

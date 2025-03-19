def load_num(file_path):

    num_set = set()
    with open(file_path, "r") as file:
        for line in file:
            num_set.add(round(float(line.strip()), 2))
    return num_set

def buscador(file_path, target):
    numbers = load_num(file_path)
    return int(target) in numbers
if __name__ == "__main__":
    file_path = "numeros.txt"
    
   

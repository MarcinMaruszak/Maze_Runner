package maze.UI;

import maze.domain.Maze;

import java.io.*;
import java.lang.invoke.SwitchPoint;
import java.util.Arrays;
import java.util.Scanner;

public class UserInterface {
    private Maze maze;
    private final Scanner scanner;
    private boolean generated;

    public UserInterface(Scanner scanner) {
        this.scanner=scanner;
        this.maze = new Maze();
        this.generated=false;
    }

    public void start(){

        label:
        while (true) {
            System.out.println();
            System.out.println("=== Menu ===");
            System.out.println("1. Generate a new maze");
            System.out.println("2. Load a maze");
            if(generated){
                System.out.println("3. Save the maze");
                System.out.println("4. Display the maze");
                System.out.println("5. Find the escape");
            }
            System.out.println("0. Exit");
            String input = scanner.nextLine();

            switch (input){
                case "0":
                    break label;
                case "1":
                    generate();
                    break;
                case "2":
                    load(scanner.nextLine());
                    break;
                case "3":
                    if(generated){
                        save(scanner.nextLine());
                    }
                    break;
                case "4":
                    if (generated) {
                        maze.print();
                    }
                    break;
                case "5":
                    if(generated){
                        maze.findEscape();
                    }
                    break;
                default:
                    System.out.println("Incorrect option. Please try again");
            }
        }
    }

    private void save(String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(fileName)))){
            oos.writeObject(maze);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load(String  fileName) {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(fileName)))){
            maze= (Maze) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("The file "+fileName  +" does not exist");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Cannot load the maze. It has an invalid format");
        }
        generated=true;
    }

    private void generate() {
        System.out.println("Please, enter the size of a maze");
        int [] size = Arrays.stream(scanner.nextLine().split(" "))
                .mapToInt(Integer::parseInt).toArray();
        if(size.length==1){
            maze.setMaze(size[0]);
        }else {
            maze.setMaze(size);
        }

        maze.generate();
        maze.print();
        generated=true;
    }
}

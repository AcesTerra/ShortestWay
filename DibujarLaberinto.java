package recorrido_de_arboles;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 * @author Alfredo Emmanuel Garcia Falcon
 */
public class DibujarLaberinto extends Application {
    
    int wHeight = 900;
    int wWidth = 900;

    int numColum;
    int numLines;

    int gridHeight;
    int gridWidth;
    
    int globalCol;
    int globalRow;
    Color globalColor;
    
    ArrayList<Color[][]> allFramesColors = new ArrayList();
    int frameCounterShow = 0;
    int frameCounterAdd = 0;
    
    Color actualColor;
    
    public static ArrayList<nodo> pila = new ArrayList();
    public static ArrayList<nodo> cola = new ArrayList();
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        ClassLoader loader = DibujarLaberinto.class.getClassLoader();
        System.out.println(loader.getResource("recorrido_de_arboles/Recorrido_de_arboles.class"));
        String filename = "C:\\Users\\achiv\\Documents\\NetBeansProjects\\CaminoCorto\\build\\classes\\caminocorto\\texto.txt";
        List<String> laberinto = new ArrayList();
        BufferedReader br = Files.newBufferedReader(Paths.get(filename));
        laberinto = br.lines().collect(Collectors.toList());
        numColum = laberinto.get(0).length();
        numLines = laberinto.size();
        gridHeight = wHeight / numColum;
        gridWidth = wWidth / numLines;
        //System.out.println(numLines + " " + numColum);
        char[][] criba = new char[numLines][numColum];
        int raizLine = 0;
        int raizColum = 0;
        
        for(int i = 0; i < numLines; i++)
        {
            for(int j = 0; j < numColum; j++)
            {
                criba[i][j] = laberinto.get(i).charAt(j);
                if(criba[i][j] == 'A')
                {
                    raizLine = i;
                    raizColum = j;
                }
            }
        }
        
        StackPane root = new StackPane();
        GridPane grid = new GridPane();
        
        Rectangle[][] cuartos = new Rectangle[numColum][numLines];
        for(int row = 0; row < numLines; row++)
        {
            for(int col = 0; col < numColum; col++)
            {
                Rectangle rect = new Rectangle();
                rect.setWidth(gridWidth);
                rect.setHeight(gridHeight);
                
                cuartos[col][row] = rect;
                GridPane.setRowIndex(rect, row);
                GridPane.setColumnIndex(rect, col);
                grid.getChildren().add(rect);
                
                //System.out.println(row + "," + col);
            }
        }
        
        Color[][] firstFrameColors = new Color[numColum][numLines];
        for(int row = 0; row < numLines; row++)
        {
            for(int col = 0; col < numColum; col++)
            {
                firstFrameColors[col][row] = Color.BLACK;
            }
        }
        allFramesColors.add(firstFrameColors);
        frameCounterAdd++;
        
        drawMaze(cuartos, criba);
        
        arbol laberintini = new arbol();
        nodo raiz = laberintini.addRoot('A', raizColum, raizLine);
        //System.out.println(raiz.dato + " " + raiz.colum + " " + raiz.line);
        cola.add(raiz);
        nodo ultimo = buscarHijos(laberintini, criba, numColum, numLines, cuartos);
        generarCamino(ultimo);
        int pilaTam = pila.size();
        for(int i = pilaTam-1; i >= 0; i--)
        {
            //System.out.println(pila.get(i).line + "," + pila.get(i).colum);
            drawPath(cuartos, pila.get(i).line, pila.get(i).colum);
        }
        
        root.getChildren().add(grid);
        
        Scene scene = new Scene(root, wHeight, wWidth);
        
        primaryStage.setTitle("Laberinto");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        Task task = new Task<Void>()
        {
            @Override
            public Void call() throws InterruptedException
            {
                while(true)
                {
                    Platform.runLater(
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                colorear(cuartos);
                            }
                    });
                    Thread.sleep(100);
                }
            }
        };
        
        
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public void drawMaze(Rectangle[][] cuartos, char[][] criba)
    {
        
        for(int row = 0; row < numLines; row++)
        {
            for(int col = 0; col < numColum; col++)
            {
                if(criba[row][col] == '0')
                {
                    addFrame(col, row, Color.BLACK);
                }
                if(criba[row][col] == '1' || criba[col][row] == 'X')
                {
                    addFrame(col, row, Color.GREEN);
                }
                if(criba[row][col] == 'A')
                {
                    addFrame(col, row, Color.BLUE);
                }
                if(criba[row][col] == 'B')
                {
                    addFrame(col, row, Color.YELLOW);
                }
            }
        }
    }
    
    public nodo buscarHijos(arbol laberintini, char[][] criba, int numColum, int numLines, Rectangle[][] cuartos)
    {
        nodo actual = cola.remove(0);
        int col = actual.colum;
        int lin = actual.line;
        if(lin-1 >= 0)
        {
            if(criba[lin-1][col] == '1')
            {
                nodo hijo = laberintini.addChild(actual, '1', col, lin-1);
                cola.add(hijo);
                criba[lin-1][col] = 'X';
                acceptedChild(cuartos, lin-1, col);
            }
            else if(criba[lin-1][col] == 'B')
            {
                nodo hijo = laberintini.addChild(actual, '1', col, lin-1);
                return hijo;
            }
        }
        if(col+1 < numColum)
        {
            if(criba[lin][col+1] == '1')
            {
                nodo hijo = laberintini.addChild(actual, '1', col+1, lin);
                cola.add(hijo);
                criba[lin][col+1] = 'X';
                acceptedChild(cuartos, lin, col+1);
            }
            else if(criba[lin][col+1] == 'B')
            {
                nodo hijo = laberintini.addChild(actual, '1', col+1, lin);
                return hijo;
            }
        }
        if(lin+1 < numLines)
        {
            if(criba[lin+1][col] == '1')
            {
                nodo hijo = laberintini.addChild(actual, '1', col, lin+1);
                cola.add(hijo);
                criba[lin+1][col] = 'X';
                acceptedChild(cuartos, lin+1, col);
            }
            else if(criba[lin+1][col] == 'B')
            {
                nodo hijo = laberintini.addChild(actual, '1', col, lin+1);
                return hijo;
            }
        }
        if(col-1 >= 0)
        {
            if(criba[lin][col-1] == '1')
            {
                nodo hijo = laberintini.addChild(actual, '1', col-1, lin);
                cola.add(hijo);
                criba[lin][col-1] = 'X';
                acceptedChild(cuartos, lin, col-1);
            }
            else if(criba[lin][col-1] == 'B')
            {
                nodo hijo = laberintini.addChild(actual, '1', col-1, lin);
                return hijo;
            }
        }
        return buscarHijos(laberintini, criba, numColum, numLines, cuartos);
    }
    
    public static void generarCamino(nodo ultimo)
    {
        pila.add(ultimo);
        if(ultimo.padre == null)
        {
            return;
        }
        generarCamino(ultimo.padre);
    }
    
    public static void posssibleChild(Rectangle[][] cuartos, int columIndex, int rowIndex)
    {
        cuartos[rowIndex][columIndex].setFill(Color.GRAY);
    }
    
    public void acceptedChild(Rectangle[][] cuartos, int columIndex, int rowIndex)
    {
        addFrame(rowIndex, columIndex, Color.RED);
    }
    
    public void drawPath(Rectangle[][] cuartos, int columIndex, int rowIndex)
    {
        addFrame(rowIndex, columIndex, Color.WHITE);
    }
    
    public void drawStepByStep(Rectangle[][] cuartos)
    {
        cuartos[globalCol][globalRow].setFill(globalColor);
    }
    
    public void colorear(Rectangle [][] cuartos){
        ArrayList<Color> colors = new ArrayList<>();
        int colorCounter = 0;
        for(int row = 0; row < numLines; row++){
            for(int col = 0; col < numColum; col++){
                colors.add(allFramesColors.get(frameCounterShow)[col][row]);
                //cuartos[col][row].setFill(tempFrameColor.get(0)[col][row]);
                //System.out.println(allFramesColors.get(frameCounterShow)[col][row]);
                //System.out.println(colors.get(colorCounter));
                colorCounter++;
            }
        }
        colorCounter = 0;
        for(int row = 0; row < numLines; row++){
            for(int col = 0; col < numColum; col++){
                cuartos[col][row].setFill(colors.get(colorCounter));
                colorCounter++;
            }
        }
        frameCounterShow++;
        //System.out.println(frameCounterShow);
        if(allFramesColors.size() == frameCounterShow)
        {
            frameCounterShow = 0;
        }
    }
    
    public void addFrame(int colToFill, int rowToFill, Color colorToFill)
    {
        Color[][] tempColor1 = new Color[numColum][numLines];
        for(int i=0; i < tempColor1.length; i++)
            for(int j=0; j < tempColor1[i].length; j++)
                tempColor1[i][j]=allFramesColors.get(frameCounterAdd-1)[i][j];
        tempColor1[colToFill][rowToFill] = colorToFill;
        allFramesColors.add(tempColor1);
        frameCounterAdd++;
    }
}

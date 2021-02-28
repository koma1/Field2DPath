package pw.komarov.field2dpath;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

public class Field2DPath {
    private int width, height;
    private int aLeft, aTop, bLeft, bTop;

    private class Step implements Comparable<Step> {
        final int left, top, number;

        Step(int left, int top, int number) {
            this.left = left;
            this.top = top;
            this.number = number;
        }

        @Override
        public String toString() {
            return String.format("%d(%d;%d)", number, left, top);
        }

        @Override
        public int compareTo(Step step) {
            return Integer.compare(this.number, step.number);
        }
    }

    public static final class CollisionException extends RuntimeException {
        CollisionException(String message) {
            super(message);
        }
    }

    private Set<Step> path;

    public Set<Step> getPath() {
        return new TreeSet<>(path);
    }

    private boolean showNumbers;

    public boolean isShowNumbers() {
        return showNumbers;
    }

    public void setShowNumbers(boolean showNumbers) {
        this.showNumbers = showNumbers;
    }

    private int[][] field;

    public Field2DPath(int width, int height) {
        this.width = width;
        this.height = height;

        field = new int[height][width];
    }

    private boolean checkBounds(int left, int top) {
        return ((left > 0) && (top > 0)) && ((left <= width)) && (top <= height);
    }

    private void throwIfBoundsExceed(int left, int top) {
        if(!checkBounds(left, top))
            throw new ArrayIndexOutOfBoundsException(String.format("Coordinates is out of Field (left %d, top: %d) width: %d, height: %d", left, top, width, height));
    }

    public Field2DPath setBrick(int left, int top) {
        throwIfBoundsExceed(left, top);

        if( //collision - "brick on point" check
                ( (aLeft >= 0) || (aTop >= 0) || (bLeft >= 0) || (bTop >= 0) ) //if points setted...
                    && //...and...
                ( //...collision have
                    ( (aLeft == left) && (aTop == top) ) // "A" collision...
                        || //...or...
                    ( (bLeft == left) && (bTop == top) ) //..."B" collision
                )
        )
            throw new CollisionException("Brick with Point collision");

        field[top - 1][left - 1] = -1;

        return this;
    }

    public Field2DPath unsetBrick(int left, int top) {
        throwIfBoundsExceed(left, top);
        field[top - 1][left - 1] = 0;

        return this;
    }

    public boolean isBrick(int left, int top) {
        throwIfBoundsExceed(left, top);

        return field[top - 1][left - 1] == -1;
    }

    public Field2DPath setRoutePoints(int aLeft, int aTop, int bLeft, int bTop) {
        throwIfBoundsExceed(aLeft, aTop);
        throwIfBoundsExceed(bLeft, bTop);

        if((field[aTop - 1][aLeft - 1]) == -1)
            throw new CollisionException("Point 'A' can't be places on the brick");
        if((field[bTop - 1][bLeft - 1]) == -1)
            throw new CollisionException("Point 'B' can't be places on the brick");

        this.aLeft = aLeft;
        this.aTop = aTop;
        this.bLeft = bLeft;
        this.bTop = bTop;

        return this;
    }

    public Field2DPath calculate() {
        //сбросим предыдущие отметки расчета на поле
        for(int i = 0; i < field.length; i++)
            for(int j = 0; j < field[i].length; j++)
                if(field[i][j] > 0)
                    field[i][j] = 0;

        //пока не достигнута точка В, добавлять в очередь следующую валидную(не кирпич и не выход за границы) точку
        final byte[] //порядок в дельте - лево, право, верх, низ
            dLeft = {-1,1,0,0},
            dTop  = {0,0,-1,1};

        Queue<Step> queue = new LinkedList<>();

        queue.offer(new Step(aLeft, aTop, 0)); //начать поиск с позиции "А"

        int wayLength = 0;
        while(!queue.isEmpty()) {
            Step s = queue.poll(); //изъяли из очереди точку...
            for(int i = 0; i <= 3; i++) { //0..3 - провернем по четырем сторонам (по дельте)
                int l = s.left + dLeft[i], t = s.top + dTop[i]; //из смещения дельты получаем координаты ячейки которую "обработаем" далее

                if(checkBounds(l,t)) //если потенциальная ячейка не выходит за границы поля...
                    if(field[t - 1][l - 1] == 0) //...значение "0" в данном случае означает что это не кирпич, а так же, что эта ячейка ранее не расчитывалась, а значит она нам интересна, продолжим обработку...
                        if((l != aLeft) || (t != aTop)) //...и если это не точка А...
                            if((l == bLeft) && (t == bTop)) { //нашли точку "В", бинго!
                                queue.clear(); //нет более необходимости в сканировании поля, цель найдена
                                wayLength = s.number; //зафиксируем количество шагов до цели
                                break; //прервем for(i)    (а вместе с ним и while..)
                            } else { //иначе, это очередная ячейка которую нужно добавить в очередь для сканирования и отметить на поле количество шагов в ней
                                field[t - 1][l - 1] = s.number + 1; //отметили на поле
                                queue.offer(new Step(l, t, s.number + 1)); //добавили в очередь
                            }
            }
        }

        if(wayLength > 0) { //если путь существует...
            path = new HashSet<>(wayLength); //создадим список с последовательными координатами пути (вместимость = длина_пути)

            Step s = new Step(bLeft, bTop, wayLength);
            for(int wayStep = wayLength; 0 < wayStep; wayStep--) {
                for(int i = 0; i <= 3; i++) {
                    int l = s.left + dLeft[i], t = s.top + dTop[i]; //из смещения дельты получаем координаты ячейки которую "обработаем" далее
                        if(field[t - 1][l - 1] == wayStep) {
                            s = new Step(l, t, wayStep);
                            path.add(s);

                            break;
                        }
                }
            }
        } else //...иначе, точки изолированы друг от друга
            path = new HashSet<>(0); //создадим список вместимостью ноль элементов, т.к. ничего добавлять в него не будем, путь не найден, а это значит, что точки изолированы друг от друга

        return this;
    }

    private int inPath(int left, int top) {
        if(path != null)
            for(Step s : path) {
                if((s.top == top) && (s.left == left))
                    return s.number;
            }

        return 0;
    }

    private boolean isCalculated() {
        return path != null;
    }

    public boolean isPointsIsolated() {
        return path.size() == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(!isCalculated() ? "Path not calculated!\n" : isPointsIsolated() ? "Points fully isolated!\n" : "");

        for(int i = 0; i < field.length; i++) { //(i+1) - top coord
            for(int j = 0; j < field[i].length; j++) { //(j+1) - left coord
                String value;

                int stepCount;

                if(field[i][j] == -1)
                    value = "x";
                else if( ((i + 1) == aTop) && ((j + 1) == aLeft) )
                    value = "A";
                else if( ((i + 1) == bTop) && ((j + 1) == bLeft) )
                    value = "B";
                else if( (stepCount = inPath(j + 1, i + 1)) > 0) //in path...
                    value = showNumbers ? String.valueOf(stepCount) : "*";
                else
                    value = " ";

                sb.append(value).append("\t");
            }

            sb.append("\n");
        }

        return sb.toString();
    }
}
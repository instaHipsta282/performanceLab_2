import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class performanceLab_2 {
    public static void main(String[] args) {
        List <Double> xList = new ArrayList<>();
        List <Double> yList = new ArrayList<>();
        BufferedReader fileReader = null;
        BufferedReader consoleReader = null;
        double neededX = 0;
        double neededY = 0;
        try {
            consoleReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please, enter X: ");
            neededX = Double.parseDouble(consoleReader.readLine());
            System.out.println("Please, enter Y: ");
            neededY = Double.parseDouble(consoleReader.readLine());
        }
        catch (IOException e) { e.printStackTrace(); }
        finally {
            if (consoleReader != null) {
                try {consoleReader.close(); }
                catch (IOException ignore) {}
            }
        }
            Point neededPoint = new Point(neededX, neededY);

        try {
            fileReader = new BufferedReader(new FileReader(args[0]));
            String s1;
            while ((s1 = fileReader.readLine()) != null) {
                String x1String = s1.split(" ")[0];
                String y1String = s1.split(" ")[1];
                Double x1 = Double.parseDouble(x1String.substring(1, x1String.length() - 1));
                Double y1 = Double.parseDouble(y1String.substring(1, y1String.length() - 1));
                xList.add(x1);
                yList.add(y1);
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        finally {
            if (fileReader != null) {
                try { fileReader.close(); }
                catch (IOException ignore) {}
            }
        }
        Point p1, p2, p3, p4; //vertices of a quadrilateral
        p1 = new Point(xList.get(0), yList.get(0));
        p2 = new Point(xList.get(1), yList.get(1));
        p3 = new Point(xList.get(2), yList.get(2));
        p4 = new Point(xList.get(3), yList.get(3));
        List<Point> pointList = new ArrayList<>();
        pointList.add(p1);
        pointList.add(p2);
        pointList.add(p3);
        pointList.add(p4);

        Side side1, side2, side3, side4; //sides of the quadrilateral
        side1 = new Side(p1, p2);
        side2 = new Side(p2, p3);
        side3 = new Side(p3, p4);
        side4 = new Side(p4, p1);
        List<Side> sidesList = new ArrayList<>();
        sidesList.add(side1);
        sidesList.add(side2);
        sidesList.add(side3);
        sidesList.add(side4);

        double minX = Math.min((Math.min(p1.x, p2.x)), (Math.min(p3.x, p4.x)));
        double maxX = Math.max((Math.max(p1.x, p2.x)), (Math.max(p3.x, p4.x)));


        int leftIntersection = leftIntersectionCount(neededPoint, maxX, minX, sidesList);
        int rightIntersection = rightIntersectionCount(neededPoint, maxX, minX, sidesList);

        wherePoint(leftIntersection, rightIntersection, pointList, neededPoint);
    }
    public static class Side {
        Point p1, p2;
        public Side(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;
        }
}
    public static class Point {
        double x, y;
        public Point(double newX, double newY) {
            x = newX;
            y = newY;
        }
    }
    public static boolean check(Point p1, Point p2, Point p3, Point p4) {
        // Начальная точка левее конечной
        if (p2.x < p1.x) {
            Point tmp = p1;
            p1 = p2;
            p2 = tmp;
        }
        if (p4.x < p3.x) {
            Point tmp = p3;
            p3 = p4;
            p4 = tmp;
        }
        //если конец левого отрезка находится левее начала правого(по оси Х)
        if (p2.x < p3.x) return false;
        // если второй отрезок вертикальный
        if (p3.x - p4.x == 0) {
            double Xa = p3.x;
            double A1 = (p1.y - p2.y) / (p1.x - p2.x);
            double b1 = p1.y - A1 * p1.x;
            double Ya = A1 * Xa + b1;
            if (p1.x <= Xa && p2.x >= Xa && Math.min(p3.y, p4.y) <= Ya && Math.max(p3.y, p4.y) >= Ya) return true;
            else return false;
        }
        // Оба отрезка невертикальные
        double A1 = (p1.y - p2.y) / (p1.x - p2.x);
        double A2 = (p3.y - p4.y) / (p3.x - p4.x);
        double b1 = p1.y - A1 * p1.x;
        double b2 = p3.y - A2 * p3.x;
        if (A1 == A2) {
            return false; //отрезки параллельны
        }
        double Xa = (b2 - b1) / (A1 - A2); //Xa - абсцисса точки пересечения двух прямых

        if ((Xa < Math.max(p1.x, p3.x)) || (Xa > Math.min( p2.x, p4.x))) {
            return false; //точка Xa находится вне пересечения проекций отрезков на ось X
        }
        else {
            return true;
        }
    }
    public static void wherePoint(int leftIntersection, int rightIntersection, List<Point> pointList, Point neededPoint) {
        boolean isTop = false;
        for (int i = 0; i < pointList.size(); i++) {
            if ((neededPoint.x == pointList.get(i).x) && (neededPoint.y == pointList.get(i).y)) {
                System.out.println("точка - вершина четырехугольника");
                isTop = true;
            }
        }
        if (leftIntersection == rightIntersection && leftIntersection != 0 && !isTop) System.out.println("точка внутри четырехугольника");
        if ((leftIntersection == 0 && rightIntersection >= 0) || (rightIntersection == 0 && leftIntersection >= 0)) System.out.println("точка снаружи четырехугольника");
        if (((leftIntersection == 1 && rightIntersection == 2) || (rightIntersection == 1 && leftIntersection == 2)) && !isTop) System.out.println("точка лежит на сторонах четырехугольника");
    }
    public static int leftIntersectionCount(Point neededPoint, double maxX, double minX, List<Side> sidesList) {
        int leftIntersection = 0;
        if (neededPoint.x > maxX) {
            for (int i = 0; i < sidesList.size(); i++) {
                if (check(new Point(minX - 1, neededPoint.y), neededPoint, sidesList.get(i).p1, sidesList.get(i).p2)) leftIntersection++;
            }
        }
        if ((neededPoint.x >= minX) && (neededPoint.x <= maxX)) {
            for (int i = 0; i < sidesList.size(); i++) {
                if (check(new Point(minX - 1, neededPoint.y), neededPoint, sidesList.get(i).p1, sidesList.get(i).p2)) leftIntersection++;

            }
        }
        return leftIntersection;
    }
    public static int rightIntersectionCount(Point neededPoint, double maxX, double minX, List<Side> sidesList) {
        int rightIntersection = 0;
        if (neededPoint.x < minX) {
            for (int i = 0; i < sidesList.size(); i++) {
                if (check(neededPoint, new Point(maxX + 1, neededPoint.y), sidesList.get(i).p1, sidesList.get(2).p2)) rightIntersection++;
            }
        }
        if ((neededPoint.x >= minX) && (neededPoint.x <= maxX)) {
            for (int i = 0; i < sidesList.size(); i++) {
                if (check(neededPoint, new Point(maxX + 1, neededPoint.y), sidesList.get(i).p1, sidesList.get(i).p2)) rightIntersection++;
            }
        }
        return rightIntersection;
    }
}
package Softheme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class Main
{
    static class Node implements Comparable<Node>, Comparator<Node>
    {
        private int weight;
        private int totalWeight;

        Node(int _weight, int _parentTotalWeight) {
            weight = _weight;
            totalWeight = _parentTotalWeight + _weight;
        }

        void setTotalWeight(Node previousNode) {
            int newWeight = weight + previousNode.weight;
            if (totalWeight < newWeight) totalWeight = newWeight;
        }

        @Override
        public int compareTo(Node o) {
            return totalWeight - o.totalWeight;
        }

        @Override
        public int compare(Node o1, Node o2) {
            return o1.totalWeight - o2.totalWeight;
        }
    }

    private static void setInheritors(LinkedList<Node> triangleRow, int[] weights, int row)
    {
        int triangleRowSize = triangleRow.size(),
            position = (row - 1) * row / 2;

        Node parent = triangleRow.getFirst();
        triangleRow.remove();
        triangleRow.add(new Node(weights[position + row], parent.totalWeight));
        triangleRow.add(new Node(weights[position + row + 1], parent.totalWeight));

        for (int i = 1; i < triangleRowSize; i++)
        {
            parent = triangleRow.getFirst();
            triangleRow.remove();
            position++;

            triangleRow.get(triangleRow.size() - 2).setTotalWeight(parent);
            triangleRow.getLast().setTotalWeight(parent);
            triangleRow.add(new Node(weights[position + row + 1], parent.totalWeight));
        }

        if (++row < (Math.sqrt(1 + 8 * weights.length) - 1) / 2) setInheritors(triangleRow, weights, row);
    }

    private static int getMaxTotalLength(int[] triangle)
    {
        LinkedList<Node> triangleRow = new LinkedList<>();
        triangleRow.add(new Node(triangle[0],0));
        setInheritors(triangleRow, triangle, 1);
        return Collections.max(triangleRow).totalWeight;
    }

    private static int[] getTriangleByURL(String URl) throws IOException
    {
        InputStream is = new URL(URl).openConnection().getInputStream();
        return new BufferedReader(new InputStreamReader(is)).lines().map(w-> w.split("\\s")).flatMap(Arrays::stream).mapToInt(Integer::parseInt).toArray();
    }

    public static void main(String[] args) throws Exception
    {
        String simpleTriangleURL = "https://dl.dropboxusercontent.com/u/28873424/tasks/simple_triangle.txt",
               triangleURL = "https://dl.dropboxusercontent.com/u/28873424/tasks/triangle.txt";

        int[] exampleTriangle = {3, 7, 4, 2, 4, 6, 8, 5, 9, 3};
        //triangleRow.setGraph(getGraphByURL(simpleTriangleURL));

        System.out.println("Triangle from example: " + getMaxTotalLength(exampleTriangle));
        System.out.println("Simple triangle: " + getMaxTotalLength(getTriangleByURL(simpleTriangleURL)));
        System.out.println("Big triangle: " + getMaxTotalLength(getTriangleByURL(triangleURL)));
    }
}

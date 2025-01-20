package org.poo.graph;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.poo.exchange.Exchange;

/**
 * This class uses a directed weighted graph in which the nodes are the inputted currencies
 */
public final class ExchangeGraph {
    private final Graph<String, DefaultWeightedEdge> exchangeGraph;

    public ExchangeGraph(final Exchange[] exchanges) {
        exchangeGraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        for (Exchange exchange : exchanges) {
            String from = exchange.getFrom();
            String to = exchange.getTo();
            double rate = exchange.getRate();
            exchangeGraph.addVertex(from);
            exchangeGraph.addVertex(to);
            DefaultWeightedEdge edge = exchangeGraph.addEdge(from, to);
            if (edge != null) {
                exchangeGraph.setEdgeWeight(edge, rate);
            }
            DefaultWeightedEdge reverseEdge = exchangeGraph.addEdge(to, from);
            if (reverseEdge != null) {
                exchangeGraph.setEdgeWeight(reverseEdge, 1.0 / rate);
            }
        }
    }

    /**
     * Uses Dijkstra's algorithm to compute a path from the begging node to the end node
     * and returns the sequentially calculated converted currency
     * @param fromCurrency The currency from that gets converted
     * @param toCurrency The currency that gets converted to
     * @param amount The amount of the currency
     * @return Converted currency
     */
    public double convertCurrency(final String fromCurrency, final String toCurrency,
                                  final double amount) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra
                = new DijkstraShortestPath<>(exchangeGraph);
        GraphPath<String, DefaultWeightedEdge> path = dijkstra.getPath(fromCurrency, toCurrency);

        if (path == null) {
            throw new IllegalArgumentException("No path found for " + fromCurrency
                                               + " and " + toCurrency);
        }

        double totalRate = 1.0;
        for (DefaultWeightedEdge edge : path.getEdgeList()) {
            double weight = exchangeGraph.getEdgeWeight(edge);
            totalRate *= weight;
        }
        return amount * totalRate;
    }

    /**
     * Helper function that quickly converts the given currency to RON
     * @param fromCurrency the currency type you want to convert
     * @param amount the amount of currency
     * @return the converted amount to ron
     */
    public double convertToRon(final String fromCurrency, final double amount) {
        return convertCurrency(fromCurrency, "RON", amount);
    }
}

/**
 * @author leo
 * @date 02/03/2015 23:38
 */
package org.systemexception.orientplayground.test;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.systemexception.orientplayground.exception.CsvParserException;
import org.systemexception.orientplayground.exception.TerritoriesException;
import org.systemexception.orientplayground.impl.DatabaseOrientImpl;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestFullItalyTerritory {

	private static DatabaseOrientImpl sut;
	private final static String dbName = "test_database_italy_territories";

	@BeforeClass
	public static void setUp() throws CsvParserException, TerritoriesException, URISyntaxException {
		URL myTestURL = ClassLoader.getSystemResource("geonames_it.csv");
		File myFile = new File(myTestURL.toURI());
		sut = new DatabaseOrientImpl();
		sut.initialSetup(dbName);
		sut.addTerritories(myFile.getAbsolutePath());
	}

	@AfterClass
	public static void tearDown() {
		sut.drop();
	}

	@Test
	public void verify_luino_has_parent_varese() throws CsvParserException, TerritoriesException {
		Vertex vertexLuino = sut.getVertexByNodeId("6540157");
		assertTrue(vertexLuino.getProperty("nodeId").equals("6540157"));
		Iterator<Edge> edgeIterator = vertexLuino.getEdges(Direction.IN, "reportsTo").iterator();
		assertTrue(edgeIterator.hasNext());
		while (edgeIterator.hasNext()) {
			assertTrue(edgeIterator.next().getVertex(Direction.OUT).getProperty("nodeId").equals("3164697"));
		}
	}

	@Test
	public void verify_varese_has_parent_lombardia() throws CsvParserException, TerritoriesException {
		Vertex vertexVarese = sut.getVertexByNodeId("3164697");
		assertTrue(vertexVarese.getProperty("nodeId").equals("3164697"));
		Iterator<Edge> edgeIterator = vertexVarese.getEdges(Direction.IN, "reportsTo").iterator();
		assertTrue(edgeIterator.hasNext());
		while (edgeIterator.hasNext()) {
			assertTrue(edgeIterator.next().getVertex(Direction.OUT).getProperty("nodeId").equals("3174618"));
		}
	}

	@Test
	public void verify_varese_has_childs() {
		Vertex vertexVarese = sut.getVertexByNodeId("3164697");
		Vertex vertexLuino = sut.getVertexByNodeId("6540157");
		List<Vertex> vertexVareseChilds = sut.getChildNodes("3164697");
		ArrayList<String> childNodes = new ArrayList<>();
		for (Vertex vertex : vertexVareseChilds) {
			childNodes.add(vertex.getProperty("nodeDesc").toString());
		}
		assertTrue(childNodes.contains("Luino"));
		assertTrue(childNodes.contains("Lavena Ponte Tresa"));
		assertTrue(childNodes.contains("Maccagno"));
	}
}

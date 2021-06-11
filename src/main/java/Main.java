import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String csvFile = "data.csv";
        List<Employee> csvlist = parserCSV(columnMapping, csvFile);
        String csvJson = listToJson(csvlist); // CSV -> JSON
        writeString(csvJson, "CSVtoJSON.json");

        String xmlFile = "data.xml"; // XML -> JSON
        List<Employee> xmlList = parserXml (xmlFile);
        String xmlJson = listToJson(xmlList);
        writeString(xmlJson, "XMLtoJSON.json");
    }

    private static List<Employee> parserCSV(String[] columnMapping, String csvFile){ // Парсер CSV
        try(CSVReader csvReader = new CSVReader (new FileReader (csvFile))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<> ();
            strategy.setType (Employee.class);
            strategy.setColumnMapping (columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee> (csvReader)
                    .withMappingStrategy (strategy)
                    .build ();

            List<Employee> listEmployee = csv.parse ();
            return listEmployee;
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return null;
    }

    public static String listToJson(List<Employee> list) { // JSON парсер
        Type listTyoe = new TypeToken<List<Employee>> () {}.getType ();
        GsonBuilder bilder = new GsonBuilder ();
        Gson gson = bilder.create ();
        String json = gson.toJson (list, listTyoe);
        System.out.println (json);
        System.out.println ("--------------\n");
        return json;
    }

    public static void writeString(String list, String csvFile) { // записываем
        try(FileWriter file = new FileWriter (csvFile)) {
            file.write (list);
            file.flush ();
        } catch (IOException e){
            e.printStackTrace ();
        }
    }

    // XML -> JSON
    public static List<Employee> parserXml(String xmlFile) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employeeList = new ArrayList<> ();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder ();
        Document doc = builder.parse(new File (xmlFile));

        // получение корневого узла документа
        Node staff = doc.getDocumentElement();
        System.out.println ("Главные элемент в XML - " + staff.getNodeName ());

        NodeList nodeList = staff.getChildNodes (); //получаем узлы с именем employee

        for (int i = 0; i < nodeList.getLength (); i++) { //создание списока объектов employee
            Node node = nodeList.item (i);
            if (Node.ELEMENT_NODE == node.getNodeType ()) {
                Element employee = (Element) node;
                NodeList nodeEmp = employee.getChildNodes();
                ArrayList<String> arEmployee = new ArrayList<> ();
                for ( int j = 0; j < nodeEmp.getLength (); j++) {
                    Node node1 = nodeEmp.item (j);
                    if (node.ELEMENT_NODE == node1.getNodeType ()) {
                        String atributeValue = node1.getTextContent ();
                        arEmployee.add (atributeValue);
//                        проверка вывода данных
//                        String atributeName = node1.getNodeName ();
//                        System.out.println (atributeName + " " + atributeValue);
                    }
                }
                System.out.println (arEmployee);
                // получаем данные для объекта
                if (arEmployee.size () == 5) {
                    long parID = Long.parseLong (arEmployee.get (0));
                    String parFistName = arEmployee.get (1);
                    String parLastName = arEmployee.get (2);
                    String parCountry = arEmployee.get (3);
                    int parAGE = Integer.parseInt (arEmployee.get (4));
                    employeeList.add (new Employee (parID, parFistName, parLastName, parCountry, parAGE));
                }
            }
        }
        return employeeList;
   }
}

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

/** Xml converter class that converts rowbased information from P|T|A|F database to xml file
 to run add desired path for rowbased .txt document and desired path for creation of xml file
 inputs: rowbased txt file
 outputs: xml file and xml string in console* */
public class writeXmlFile {
	
	//path for input rowbased txt dokument
	static String rowinputPath ;
	//path for output xml document to be created
	static String xmlPath ;	
	
	/** adds familymember to documentTree aswell as familymembers phone/adress if available 
	 * 
	 * @param iterator
	 * @param doc
	 * @param personElement
	 */
	public static void addFamily(ListIterator<String> iterator, Document doc, Element personElement) {
		Element family = doc.createElement("family");				// create element for family as child of person
		personElement.appendChild(family);
		
		Element name = doc.createElement("name");					// create element for name of family
		family.appendChild(name);
        name.appendChild(doc.createTextNode(iterator.next()));
        
        Element born = doc.createElement("born");					// create element for birth of familymember
        family.appendChild(born);
        born.appendChild(doc.createTextNode(iterator.next()));
        
        // check for additional information on family
        String current = null;
        if ( iterator.hasNext()) {
        	current = iterator.next();										// if next value exists store as current value
        }
        // logic for check if adress or phone or both exists for familymember regardess of order
        while (iterator.hasNext() && !(current.equals("P") || current.equals("F"))) {
        	if (current.equals("A")) {
            	addFamilyAdress(iterator,doc,family);							// if adress exist add adress of family as child of family
            }
        	if (current.equals("T")) {
            	addFamilyPhone(iterator,doc,family);							// if adress exist add adress of family as child of family
            }
        	if (iterator.hasNext()) {
        		current = iterator.next();
        	}       	
        }
        iterator.previous();												//return iterator to previous iteration for logic in newPerson()

        
	}
	/** adds adress to familymember in documentTree 
	 * 
	 * @param iterator
	 * @param doc
	 * @param familyElement
	 */
	public static void addFamilyAdress(ListIterator<String> iterator, Document doc, Element familyElement) {
		// adds family members adress to document
        Element adress = doc.createElement("adress");			// create element for adress as child of family
		familyElement.appendChild(adress);

		Element street = doc.createElement("street");			// create element for street as child of adress
		adress.appendChild(street);
        street.appendChild(doc.createTextNode(iterator.next()));

        Element city = doc.createElement("city");				// create element for city as child of adress
        adress.appendChild(city);
        city.appendChild(doc.createTextNode(iterator.next()));
                
// logic for checking if postcode exists
        
        String current = null;									// current value
        
        // if next exists check for postcode
        if (iterator.hasNext()) {
        	current = iterator.next();									
        }
        if (iterator.hasNext() && !(current.equals("P") || current.equals("F") || current.equals("T"))) {
            Element postalcode = doc.createElement("postalcode");	// create element for postalcode as child of adress
            adress.appendChild(postalcode);
            postalcode.appendChild(doc.createTextNode(iterator.next()));
        }else if (iterator.hasNext()){
        	iterator.previous();													// if postcode does not exist end loop on previous value

        }
        
	}
	/** adds phonenumbers for familymember in documentTree 
	 * 
	 * @param iterator
	 * @param doc
	 * @param familyElement
	 */
	public static void addFamilyPhone(ListIterator<String> iterator, Document doc, Element familyElement) {
		/* adds family members phone to document */
        Element phone = doc.createElement("phone");				// create element for phone as child of family
		familyElement.appendChild(phone);

		Element mobile = doc.createElement("mobile");			// create element for mobile as child of family/phone
		phone.appendChild(mobile);
        mobile.appendChild(doc.createTextNode(iterator.next()));

        Element home = doc.createElement("home");				// create element for home as child of family/phone
        phone.appendChild(home);
        home.appendChild(doc.createTextNode(iterator.next()));
        
	}
	/** adds phonenumber for person in documentTree 
	 * 
	 * @param iterator
	 * @param doc
	 * @param personElement
	 */
	public static void addPhone(ListIterator<String> iterator, Document doc, Element personElement) {
        Element phone = doc.createElement("phone");				// create element for phone
		personElement.appendChild(phone);

		Element mobile = doc.createElement("mobile");			// adds a mobile element as child of phone
		phone.appendChild(mobile);
        mobile.appendChild(doc.createTextNode(iterator.next()));

        Element home = doc.createElement("home");				// adds a home element as child of phone
        phone.appendChild(home);
        home.appendChild(doc.createTextNode(iterator.next()));
        
	}
	/** adds adress to person in documentTree 
	 * 
	 * @param iterator
	 * @param doc
	 * @param personElement
	 */
	public static void addAdress(ListIterator<String> iterator, Document doc, Element personElement) {
        Element adress = doc.createElement("adress");			// create element for adress
		personElement.appendChild(adress);

		Element street = doc.createElement("street");			// adds a street element as child of adress
		adress.appendChild(street);
        street.appendChild(doc.createTextNode(iterator.next()));

        Element city = doc.createElement("city");				// adds city element as child of adress
        adress.appendChild(city);
        city.appendChild(doc.createTextNode(iterator.next()));
        
        
        // logic for checking if postcode exists
        
        String current = null;									// current value
        
        // if next exists check for postcode
        if (iterator.hasNext()) {
        	current = iterator.next();									
        }
        if (iterator.hasNext() && !(current.equals("P") || current.equals("F") || current.equals("T"))) {
        	Element postalcode = doc.createElement("postalcode");			// adds postcode element as child of adress
            adress.appendChild(postalcode);
            postalcode.appendChild(doc.createTextNode(current));
        }else if (iterator.hasNext()){
        	iterator.previous();													// if postcode does not exist end loop on previous value

        }
        
	}
	/** creates new people in the document tree and adds adress/family/phone and next person in the data 
	 * 
	 * @param iterator
	 * @param doc
	 * @param rootElement
	 */
	public static void newPerson(ListIterator<String> iterator, Document doc, Element rootElement) {
		Element person = doc.createElement("person");						// create element for person as child of root
		rootElement.appendChild(person);
		
		Element firstName = doc.createElement("firstName");					// add element of firstname as child of person
		person.appendChild(firstName);
        firstName.appendChild(doc.createTextNode(iterator.next()));
        
        Element lastName = doc.createElement("lastName");					// adds element of lastname as child of person
        person.appendChild(lastName);
        String data = iterator.next();
        lastName.appendChild(doc.createTextNode(data));

        // check for additional information of person
		while (iterator.hasNext()  ) {
			
			data =iterator.next();
			//System.out.print(data);			
			if (data.equals("A")) {
				addAdress(iterator,doc,person);									// if adress is found add adress to person
			}else if (data.equals("T")) {
				addPhone(iterator,doc,person);										// if phone is found add phone to person	
			}else if (data.equals("F")) {
				addFamily(iterator,doc ,person);								// if family is found add family to person
			}else if (data.equals("P")) {
				newPerson(iterator,doc ,rootElement);								// if new person is found create new person
			}
	
		}
		
		
	}
	/** creates document with first people as rootElement
	 * searches for the first person in the list and starts adding the row based information into a tree
	 * @param doc
	 * @param list
	 */
	public static void elementsXml(Document doc, ArrayList<String> list) {
		
		//Elements
        // root element
        Element rootElement = doc.createElement("people");					// create rootElement of people
        doc.appendChild(rootElement);										// add rootElement to document
				
		ListIterator<String> i = list.listIterator();						// create iterator for iteration of Arraylisg of row data
		
		// Search data for first person
		while( i.hasNext()) {
			if (i.next().equals("P")) {
				newPerson(i,doc,rootElement);								//add Person found as newPerson
			}
		}
	        
        
	}
	/** main method for row -> xml conversion 
	 * 
	 * @param argv
	 * @throws FileNotFoundException
	 */
	public static void main(String argv[]) throws FileNotFoundException {
		
		@SuppressWarnings("resource")
		Scanner inScanner = new Scanner(System.in);
		System.out.print("Enter input file path:");
		String inFile = inScanner.next();
		System.out.println("You entered: " + inFile);   
		rowinputPath = inFile;
		
		System.out.print("Enter desired output file path:");
		String outFile = inScanner.next();
		System.out.println("You entered: " + outFile);
		xmlPath = outFile;
		
				
		//Read data from txt to arraylist
		// Path to .txt document with rowbased data
		String delimiter = "\\r\\n|\\|"; 							// set delimiter for inputfile
		Scanner s = new Scanner(new File(rowinputPath)).useDelimiter(delimiter);
		// create arraylist 
		ArrayList<String> list = new ArrayList<String>();		
		while (s.hasNext()){
				list.add(s.next());								// add data from scanner to arraylist
		}
		s.close();  
		// end reading of data from txt to arraylist

		// Creation of Xml file
	      try {
	    	  // creation of document for later creation of DOMtree 
	    	 DocumentBuilderFactory dbFactory =
	         DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.newDocument();
	                  
	         // add elements of Arraylist txt data to document
	         elementsXml(doc,list);
	         
	         // Write the content of Document into DOM tree
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
	         Transformer transformer = transformerFactory.newTransformer();
	         transformer.setOutputProperty(OutputKeys.INDENT, "yes");							// Set output indent
	         DOMSource source = new DOMSource(doc);
	         
	         // Create xml file
	         File file = new File(xmlPath);
	         if(!file.exists()) {
	             System.out.println("creating file");
	             if(file.createNewFile()) {
	             System.out.println("Succesfully created file at "  + file.getCanonicalFile());
	             } else{
	             System.out.println("Failed to create file");
	             }
	         }
	         
	         // write data from document to xmlfile	            
	         StreamResult result = new StreamResult(file);
	         transformer.transform(source, result);
	         System.out.println("");
	         System.out.println("data sucessfully converted to xml, output data can be found at: ");
	         System.out.println(xmlPath);
	         
	         
	         /* Output xml data to console for testing
	         StreamResult consoleResult = new StreamResult(System.out);
	         transformer.transform(source, consoleResult);
	         */
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      
	      System.out.println("Program end");
	   }
	

	
}

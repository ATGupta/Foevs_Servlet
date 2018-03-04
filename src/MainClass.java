import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MainClass
 */
@WebServlet("/foevs")
public class MainClass extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private String parentDir;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MainClass() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		PrintWriter sw = response.getWriter();
		// pw.append("Served at: ").append(request.getContextPath());

		ServletContext context = getServletContext();
		parentDir = context.getRealPath("Resources_FOEVS_ATG");
		
		String un=request.getParameter("un");
		String ch=request.getParameter("ch");
		
		if(un==null)
			createWebpage("data",sw);
		else if(un.equalsIgnoreCase("acknowledgement"))
			createWebpage("acknowledgement/ack",sw);
		else if(un!=null && ch!=null)
			createWebpage(un+"/"+ch+"/data",sw);
		else
			createWebpage("data",sw);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private void createWebpage(String path, PrintWriter sw) throws IOException{
		printData(readFun("page_top"), sw, path);
		printData(readFun(path), sw, path);
		printData(readFun("page_bottom"), sw, path);
	}

	private BufferedReader readFun(String path) throws IOException {
		path = parentDir + "/" + path;
		BufferedReader br = new BufferedReader(new FileReader(path));
		return br;
	}

	private void printData(BufferedReader br, PrintWriter sw, String path) throws IOException {
		path=path.substring(0, path.length()-4);//remove "data" file
		for (;;) {
			String st = br.readLine();
			if (st == null)
				break;
			
			if(st.startsWith("\\takefrom")) {
				takeFrom(st, sw);
				continue;
			}
			else if(st.startsWith("\\image")) {
				st=st.substring(7);
				st= "Resources_FOEVS_ATG/" + path + st;//path already contains "/" at the end
				sw.println("<p style=\"text-align: center\"><img style=\"height: 300px\" src=\""
						+st
						+"\">");
				continue;
			}
			sw.println(st);
		}
	}
	
	private void takeFrom(String st, PrintWriter sw) throws IOException{
		st=st.substring(9);
		if(st.equals("chapterMenu")) {
			takeFromChapterMenu(st,sw);
		}
	}
	
	private void takeFromChapterMenu(String st, PrintWriter sw) throws IOException{
		BufferedReader br=readFun(st);
		int tab=-1;
		int row=0;
		String unit="";
		for(;;) {
			String a=br.readLine();
			
			if(a==null) {
				sw.println("</ul>\n</li>\n</ul>");
				break;
			}
			//a=a.toUpperCase();
			
			if(a.startsWith("\t")) {
				a=a.substring(1);
				if(tab==0) {
					sw.println("<ul>");
					tab=1;
				}
				sw.println("<li><a href=\"?un="+unit.replaceAll("\\s", "")+"&ch="+a.replaceAll("\\s", "")+"\">"+a+"</a></li>");
			}
			else {
				unit=a;
				if(tab==-1) {
					sw.println("<ul>");
					tab=0;
				}
				else if(tab==1) {
					sw.println("</ul>\n</li>");
					tab=0;
				}
				sw.println("<li><a href=\"\">"+a+" &#9656</a>");
			}
		}
	}
}

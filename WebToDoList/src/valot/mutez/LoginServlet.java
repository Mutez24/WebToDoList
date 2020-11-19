package valot.mutez;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * Servlet implementation class StudentControllerServlet
 */
@WebServlet("/LoginServlet")
//the � LoginServlet � is called when the url 
//� http://localhost:8081/WebToDoList/LoginServlet � is entered in the browser.
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserDBManager userDBManager;
	@Resource(name="jdbc/todolists")
	private DataSource dataSource;


	//D'abord, on regarde si des cookies sont pr�sents dans le navigateur afin de compl�ter
	//automatiquement les champs username et password.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie [] cookies = request.getCookies();
		if(cookies!= null){
			for(Cookie cookie:cookies){
					if(cookie.getName().equals("username"))
						request.setAttribute("username", cookie.getValue()) ;
					if(cookie.getName().equals("password"))
						request.setAttribute("password", cookie.getValue()) ;
			}
		}
		request.getRequestDispatcher("/login.jsp").forward(request, response);
		try {
			
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//S'ex�cute lorsque l'on clique sur le bouton login.
	//D'abord on r�cup�re ce que le client a rentr� dans les champs de texte username et password.
	//Ensuite on v�rifie si ses identifiants et mdp sont bien dans la BDD.
	//Si c'est le cas, alors on cr�e une session dans laquelle on enregistre son ID afin de pouvoir r�cup�rer 
	//ses donn�es dans toutes les autres servlets � l'aide de la fonction fetchUserByID.
	//On cr�e ensuite le cookie (on ne le fait pas avant la v�rification des credentials afin de ne pas cr�er un cookie
	//pour un utilisateur non enregistr� dans notre BDD.
	//Enfin on redirige l'utilisateur vers la main page.
	//Si les identifiants sont erron�s, alors l'utilisateur est renvoy� vers une page d'erreur qui affiche un message 
	//d'erreur (unique) et propose soit de se rediriger vers la page login pour retenter de se connecter, soit de se rediriger 
	//vers la page registration qui lui permettra de se cr�er un compte.
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String username= request.getParameter("username");
		String password= request.getParameter("password");
		try {
			if(CheckCredentials(username, password)) {
				//Cr�ation de la session 
				HttpSession session = request.getSession();
				User current_user = userDBManager.fetchUserByUsername(username);
				session.setAttribute("ID", current_user.getId());
				
				//Cr�ation du cookie pour username et password pour l'autocompl�tion en cas de reconnexion (sous 24h)
				Cookie user_cookie = new Cookie("username", username);
				Cookie password_cookie = new Cookie("password",password);
				user_cookie.setMaxAge(60*60*24) ; 
				password_cookie.setMaxAge(60*60*24);
				response.addCookie(user_cookie) ;
				response.addCookie(password_cookie) ;
				
				response.sendRedirect("ToDoServlet");
			}
			else {
				request.getRequestDispatcher("/error.jsp").forward(request, response);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		userDBManager = new UserDBManager(dataSource);
	}
	
	//M�thode simple qui permet de v�rifier si un utilisateur appartient bien � la BDD.
	//On prend en param�tres le username et password de l'utilisateur et on va chercher
	//ce user avec la fonction fetchUserByUsernamePassword qui renvoie un user si la combinaison des 
	//deux existe. Sinon, elle renvoie null et dans ce cas on donne la valeur false a confirmed avant de le return.
	public boolean CheckCredentials(String username, String password) throws Exception {
		boolean confirmed=true;
		if(userDBManager.fetchUserByUsernamePassword(username,password)==null) {
			confirmed=false;
		}
		return confirmed;
	}
}


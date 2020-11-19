package valot.mutez;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private UserDBManager userDBManager;
	@Resource(name="jdbc/todolists")
	private DataSource dataSource;


	//Pas d'action particuli�re au moment d'arriver sur la page register.jsp
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.getRequestDispatcher("/register.jsp").forward(request, response);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//M�thode ex�cut�e au moment de cliquer sur le bouton Register.
	//On r�cup�re les infos rentr�es dans les zones de texte et on cr�e un nouveau user avec ces donn�es.
	//Ensuite il faut faire 2 v�rifications:
	//la premi�re �tant de v�rifier si le username rentr� n'est pas d�j� utilis� par un autre user dans la DB.
	//la deuxi�me �tant de v�rifier si le password correspond bien au confirmed password.
	//Si la v�rification est valid�e, le nouvel utilisateur et toutes ses infos sont ajout�es � la DB.
	//Ensuite un cookie est cr�e avant de renvoyer l'utilisateur vers la page login o� il n'aura plus qu'� appuyer
	//sur le bouton login vu que les champs username et password seront deja remplis grace au cookie.
	//Si la v�rification ne passe pas on renvoie vers la page d'erreur.
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String username= request.getParameter("username");
		String password= request.getParameter("password");
		String confirmed_password=request.getParameter("cfpassword");
		String firstName= request.getParameter("name");
		String lastName=request.getParameter("surname");		
		
		User user = new User(firstName, lastName, username,password,false);
		
		try {
			if(CheckUser(username) && confirmed_password.equals(password)) {
				userDBManager.Add(user);
				
				Cookie user_cookie = new Cookie("username", username);
				Cookie password_cookie = new Cookie("password",password);
				user_cookie.setMaxAge(60*60*24) ; // in seconds, here for 24 hours
				password_cookie.setMaxAge(60*60*24);
				response.addCookie(user_cookie) ;
				response.addCookie(password_cookie) ;
				
				response.sendRedirect("LoginServlet");
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
	
	//M�thode qui check si le username de l'utilisateur est d�j� utilis� par un autre utilsateur de la DB.
	//Renvoie true si le username est d�j� utilis� et false sinon.
	public boolean CheckUser(String username) throws Exception {
		boolean new_user=false;
		if(userDBManager.fetchUserByUsername(username)==null) {
			new_user=true;
		}
		return new_user;
	}
	
}


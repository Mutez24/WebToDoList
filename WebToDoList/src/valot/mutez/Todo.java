package valot.mutez;

//Classe Todo qui a pour attribut:
//  -un id qui le diff�rencie de tous les autres
//  -une description
//  -l'id du cr�ateur de ce todo
//  -l'id du user � qui la todo est attribu�e

public class Todo {
	private int id;
	private String description;
	private int creator_id;
	private int user_id;
	
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}

	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}

	public int getCreator_id() {return creator_id;}
	public void setCreator_id(int creator_id) {this.creator_id = creator_id;}

	public int getUser_id() {return user_id;}
	public void setUser_id(int user_id) {this.user_id = user_id;}

	public Todo(int id, String description, int creator_id, int user_id) {
		super();
		this.id = id;
		this.description = description;
		this.creator_id = creator_id;
		this.user_id=user_id;
	}
	
	//Surcharge de la classe pour cr�er un todo uniquement en fonction de sa description et de l'id du cr�ateur.
	//Cela est utile lorsque l'on a pas encore d'id pour le todo ( car ce dernier est cr�e au moment de la cr�ation
	//dans la DB) et lorsque l'on ne veut pas donner cette tache � un user en particulier.
	public Todo(String description, int id_creator) {
		super();//lier � la classe dont c'est d�riv�
		this.description = description;
		this.creator_id=id_creator;
	}
}


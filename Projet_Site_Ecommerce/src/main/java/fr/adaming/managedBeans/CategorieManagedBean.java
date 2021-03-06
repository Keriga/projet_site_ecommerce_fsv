package fr.adaming.managedBeans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
//import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.UploadedFile;

import fr.adaming.model.Categorie;
import fr.adaming.service.ICategorieService;

@ManagedBean(name = "catMB")
@ViewScoped
public class CategorieManagedBean implements Serializable {

	// Transformation de l'association UML en Java
	@ManagedProperty(value = "#{catService}")
	private ICategorieService categorieService;

	// Setter pour l'injection de d�pendance
	public void setCategorieService(ICategorieService categorieService) {
		this.categorieService = categorieService;
	}

	// Attributs
	private Categorie categorie;
	private List<Categorie> listeCategories;
	private boolean indice; // pour le rendered de la vue
	private HttpSession session;
	private UploadedFile uf;

	// Constructeur vide
	public CategorieManagedBean() {
		this.categorie = new Categorie();
		this.indice = false;
	}

	@PostConstruct
	public void init() {
		session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	}

	// Getters et setters
	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public List<Categorie> getListeCategories() {
		return listeCategories;
	}

	public void setListeCategories(List<Categorie> listeCategories) {
		this.listeCategories = listeCategories;
	}

	public boolean isIndice() {
		return indice;
	}

	public void setIndice(boolean indice) {
		this.indice = indice;
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public UploadedFile getUf() {
		return uf;
	}

	public void setUf(UploadedFile uf) {
		this.uf = uf;
	}

	// M�thodes m�tiers
	// Consulter toutes les cat�gories
	public String consulterTout() {
		this.listeCategories = categorieService.getAllCategorie();
		this.session.setAttribute("listeCat", listeCategories);
		return "categories";
	}

	// Ajout cat�gorie
	public String ajouterCategorie() {
		// Ajouter la photo dans la cat�gorie
		categorie.setPhoto(uf.getContents());
		// Appel de la m�thode service
		Categorie catAjout = categorieService.addCategorie(categorie);
		if (catAjout.getId() != 0) {
			// R�cup�rer la liste des cat�gories mise � jour
			listeCategories = categorieService.getAllCategorie();
			this.session.setAttribute("listeCat", listeCategories);
			return "espaceadmin";
		} else {
			// Si erreur
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Erreur dans l'ajout."));
			return "ajoutadmin";
		}
	}

	// Modifier cat�gorie
	public String modifierCategorie() {
		int verif = categorieService.updateCategorie(categorie);
		if (verif != 0) {
			// R�cup�rer la liste des cat�gories mises � jour
			listeCategories = categorieService.getAllCategorie();
			return "#";
		} else {
			// Si erreur
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La cat�gorie indiqu�e n'existe pas."));
			return "#";
		}
	}

	// Supprimer cat�gorie
	public void supprimerCategorie() {
		int verif = categorieService.deleteCategorie(categorie);
		if (verif != 0) {
			// R�cup�rer la liste des cat�gories mises � jour
			listeCategories = categorieService.getAllCategorie();
			// L'ajouter � la session
			session.setAttribute("listeCat", listeCategories);
		} else {
			// Si erreur
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La cat�gorie indiqu�e n'existe pas."));
		}
	}

	// Rechercher cat�gorie
	public String consulterCategorie() {
		Categorie cOut = categorieService.getCategorieById(categorie);
		if (cOut != null) {
			categorie = cOut;
			indice = true;
			return "#";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("La cat�gorie recherch�e n'existe pas."));
			indice = false;
			return "#";
		}
	}

	// Modification de la cat�gorie avec rowEditor
	public void onRowEdit(RowEditEvent event) {
		categorieService.updateCategorie((Categorie) event.getObject());
		listeCategories = categorieService.getAllCategorie();
		session.setAttribute("listeCat", listeCategories);
		FacesMessage msg = new FacesMessage("Cat�gorie modifi�e", ((Categorie) event.getObject()).getNomCategorie());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onRowCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Modification annul�e", ((Categorie) event.getObject()).getNomCategorie());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	// Modifier seulement la photo de la cat�gorie
	public String modifierPhotoCategorie() {
		
		
		System.out.println("Je suis ici");
		// R�cup�rer la cat�gorie avec l'id
		categorie = categorieService.getCategorieById(categorie);
		// Modifier la photo dans la cat�gorie
		categorie.setPhoto(uf.getContents());
		int verif = categorieService.updateCategorie(categorie);
		if (verif != 0) {
			System.out.println("Je suis ici");
			// R�cup�rer la liste des cat�gories mises � jour
			listeCategories = categorieService.getAllCategorie();
			// Actualiser la liste dans la session
			session.setAttribute("listeCat", listeCategories);
			return "ajoutadmin";
		} else {
			// Si erreur
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La cat�gorie n'a pas �t� modifi�e."));
			return "ajoutadmin";
		}
	}
	
	// Redirection test
	public String testNavig(){
		return "accueil";
	}
}

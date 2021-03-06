package fr.adaming.dao;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import fr.adaming.model.Categorie;
import fr.adaming.model.Produit;

@Repository
public class ProduitDaoImpl implements IProduitDao {
	@Autowired
	private SessionFactory sf;

	private void setSf(SessionFactory sf) {
		this.sf = sf;
	}

	@Override
	public Produit addProduit(Produit prod) {
		// Ouvrir une session
		Session s = sf.getCurrentSession();

		// Ajouter � la BD
		s.save(prod);

		// Obtenir le produit de sortie
		return prod;
	}

	@Override
	public List<Produit> getlisteProduit() {
		// Ouvrir une session
		Session s = sf.getCurrentSession();

		// Requete HQL
		String req = "FROM Produit p";

		// Cr�er lequery
		Query query = s.createQuery(req);

		List<Produit> liste = query.list();

		return liste;
	}

	@Override
	public int updateProduit(Produit prod) {
		// Ouvrir une session
		Session s = sf.getCurrentSession();

		// Requete HQL
		String req = "UPDATE Produit p SET p.nomProduit = :pNomP, p.description = :pDescr, "
				+ "p.prix = :pPrix, p.quantite = :pQuant, p.selectionne = :pSel, "
				+ "p.photo = :pPhoto WHERE p.id = :pId";

		// Cr�er le query
		Query query = s.createQuery(req);

		// Passage des parametres
		query.setParameter("pNomP", prod.getNomProduit());
		query.setParameter("pDescr", prod.getDescription());
		query.setParameter("pPrix", prod.getPrix());
		query.setParameter("pQuant", prod.getQuantite());
		query.setParameter("pSel", prod.isSelectionne());
		query.setParameter("pPhoto", prod.getPhoto());
		query.setParameter("pId", prod.getId());

		return query.executeUpdate();
	}

	@Override
	public int deleteProduit(Produit prod) {
		// Ouvrir une session
		Session s = sf.getCurrentSession();

		// Requete HQL
		String req = "DELETE FROM Produit p WHERE p.id = :pId";

		// Cr�er le query
		Query query = s.createQuery(req);

		// Passage des parametres
		query.setParameter("pId", prod.getId());

		return query.executeUpdate();
	}

	@Override
	public Produit rechercherProduit(Produit pr) {
		// Ouvrir une session
		Session s = sf.getCurrentSession();
		// Requete HQL
		String req = "FROM Produit p WHERE p.id = :pId";
		// Cr�er le query
		Query query = s.createQuery(req);
		// Passage des parametres
		query.setParameter("pId", pr.getId());
		// R�cup�rer le r�sultat
		Produit pOut = (Produit) query.uniqueResult();
		// Chargement de l'image
		pOut.setImage("data:image/png;base64,"+Base64.encodeBase64String(pOut.getPhoto()));
		return pOut;
	}

	@Override
	public List<Produit> produitByCategorie(Categorie cat) {
		// Ouvrir une session
		Session s = sf.getCurrentSession();
		// Requete HQL
		String req = "FROM Produit p WHERE p.categorie.id = :pId";
		// Cr�er le query
		Query query = s.createQuery(req);
		// Passage des parametres
		query.setParameter("pId", cat.getId());
		// R�cup�ration du r�sultat
		List<Produit> listeOut = query.list();
		// Chargement des images
		for (Produit prod : listeOut) {
			prod.setImage("data:image/png;base64," + Base64.encodeBase64String(prod.getPhoto()));
		}
		// retourner le r�sultat
		return listeOut;
	}

}

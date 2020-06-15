package br.com.caelum;

import br.com.pm73.dao.CriadorDeSessao;
import br.com.pm73.dao.UsuarioDao;
import br.com.pm73.dominio.Usuario;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class UsuarioDaoTest {

	
	private Session session;
	private UsuarioDao usuarioDao;

	@Before
	public void antes() {
		session = new CriadorDeSessao().getSession();
		usuarioDao = new UsuarioDao(session);
		session.beginTransaction();
	}
	
	@After
	public void depois() {
		session.getTransaction().rollback();
		session.close();
	}

	// testa sql, faz inserção e verifica se foi inserido através do sql
	@Test
	public void deveEncontrarEmailMokado() {
        
        Usuario usuario = new Usuario("Joao", "diego@.com.br");
        usuarioDao.salvar(usuario);
        
        Usuario usuarioDoBAnco = usuarioDao.porNomeEEmail("Joao","diego@.com.br");

        Assert.assertEquals(usuario.getNome(), usuarioDoBAnco.getNome());
        Assert.assertEquals(usuario.getEmail(), usuarioDoBAnco.getEmail());
	}

	// compara o retorno
	@Test
	public void deveEncontrarPeloNomeEEmail() {
		Usuario novoUsuario = new Usuario("João da Silva", "joao@dasilva.com.br");
		
		usuarioDao.salvar(novoUsuario);
		
		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("João da Silva", "joao@dasilva.com.br");
		Assert.assertEquals("João da Silva", usuarioDoBanco.getNome());
		Assert.assertEquals("joao@dasilva.com.br", usuarioDoBanco.getEmail());
	}

	// retorna objeto nullo
	@Test
	public void deveRetornarNuloSeNaoEncontrarUsuario() {
		Usuario uso = usuarioDao.porNomeEEmail("João Joaquim", "joao@joaquim.com.br");
		Assert.assertNull(uso);
	}
}

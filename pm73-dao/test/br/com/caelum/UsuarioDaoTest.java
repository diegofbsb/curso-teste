package br.com.caelum;

import br.com.pm73.dao.CriadorDeSessao;
import br.com.pm73.dao.LeilaoDao;
import br.com.pm73.dao.UsuarioDao;
import br.com.pm73.dominio.Leilao;
import br.com.pm73.dominio.Usuario;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UsuarioDaoTest {

	private Session session;
	private UsuarioDao usuarioDao;
	private LeilaoDao leilaoDao;

	@Before
	public void antes() {
		session = new CriadorDeSessao().getSession();
		usuarioDao = new UsuarioDao(session);
		leilaoDao = new LeilaoDao(session);
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

		Usuario usuarioDoBAnco = usuarioDao.porNomeEEmail("Joao", "diego@.com.br");

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

	@Test
	public void deveDeletarUmUsuario() {
		Usuario usuario = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");
		usuarioDao.salvar(usuario);
		usuarioDao.deletar(usuario);
		// envia tudo para o banco de dados
		session.flush();
		Usuario usuarioNoBanco = usuarioDao.porNomeEEmail("Mauricio Aniche", "mauricio@aniche.com.br");
		assertNull(usuarioNoBanco);

	}

	@Test
	public void deveDeletarUmUsuarioPorId() {
		Usuario usuario = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");
		Leilao leilao = new Leilao("Diego", 100.0, usuario, false);
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(leilao);
		// envia tudo para o banco de dados
		session.flush();
		leilaoDao.deleta(leilao);
		assertNull(leilaoDao.porId(leilao.getId()));

	}

	@Test
	public void testaAlterar() {
		Usuario usuario = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");
		usuarioDao.salvar(usuario);

		usuario.setNome("João da Silva");
        usuario.setEmail("joao@silva.com.br");
		usuarioDao.atualizar(usuario);
		session.flush();
		
		Usuario novoUsuario = usuarioDao.porNomeEEmail("João da Silva", "joao@silva.com.br");
		assertNotNull(novoUsuario);
		
		System.out.println(novoUsuario);

		Usuario usuarioInexistente = usuarioDao.porNomeEEmail("Mauricio Aniche", "mauricio@aniche.com.br");
		assertNull(usuarioInexistente);
	}
}

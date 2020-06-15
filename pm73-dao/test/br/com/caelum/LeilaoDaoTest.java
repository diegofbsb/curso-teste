package br.com.caelum;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.pm73.dao.CriadorDeSessao;
import br.com.pm73.dao.LeilaoDao;
import br.com.pm73.dao.UsuarioDao;
import br.com.pm73.dominio.Leilao;
import br.com.pm73.dominio.Usuario;

public class LeilaoDaoTest {

	private Session session;
	private LeilaoDao leilaodao;
	private UsuarioDao usuarioDao;

	@Before
	public void antes() {
		session = new CriadorDeSessao().getSession();
		usuarioDao = new UsuarioDao(session);
		leilaodao = new LeilaoDao(session);
		session.beginTransaction();
	}

	@After
	public void depois() {
		session.getTransaction().rollback();
		session.close();
	}

	@Test
	public void totalTest() {
		Usuario usu = new Usuario("Joao", "Joao@.com.br");
		Leilao ativo = new Leilao("ddd", 1500.0, usu, false);
		Leilao encerrado = new Leilao("yyy", 1500.0, usu, false);
		encerrado.encerra();

		usuarioDao.salvar(usu);
		leilaodao.salvar(ativo);
		leilaodao.salvar(encerrado);

		long total = leilaodao.total();
		assertEquals(1L, total);

	}

	@Test
	public void deveTrazerSomenteLeiloesAntigos() {
		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		Leilao recente = new Leilao("XBox", 700.0, mauricio, false);
		Leilao antigo = new Leilao("Geladeira", 1500.0, mauricio, true);

		Calendar dataRecente = Calendar.getInstance();
		Calendar dataAntiga = Calendar.getInstance();
		dataAntiga.add(Calendar.DAY_OF_MONTH, -10);

		recente.setDataAbertura(dataRecente);
		antigo.setDataAbertura(dataAntiga);

		usuarioDao.salvar(mauricio);
		leilaodao.salvar(recente);
		leilaodao.salvar(antigo);

		List<Leilao> antigos = leilaodao.antigos();

		assertEquals(1, antigos.size());
		assertEquals("Geladeira", antigos.get(0).getNome());
	}
	
	@Test
	public void trazerLeiloesNaoEncerredos() {
		Calendar inicio = Calendar.getInstance();
		inicio.add(Calendar.DAY_OF_MONTH, -10);
		
		Calendar fim = Calendar.getInstance();
		
		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");
		Leilao leilao1 = new Leilao("XBox", 700.0, mauricio, false);
		Calendar dateLeilao1 = Calendar.getInstance();
		inicio.add(Calendar.DAY_OF_MONTH, -2);
		leilao1.setDataAbertura(dateLeilao1);
		Leilao leilao2 = new Leilao("Geladeira", 1500.0, mauricio, true);
		Calendar dateLeilao2 = Calendar.getInstance();
		inicio.add(Calendar.DAY_OF_MONTH, -20);
		leilao1.setDataAbertura(dateLeilao2);
		
		usuarioDao.salvar(mauricio);
		leilaodao.salvar(leilao1);
		leilaodao.salvar(leilao2);
		
		List<Leilao> leiloes = leilaodao.porPeriodo(inicio, fim);
		
		assertEquals(2, leiloes.size());
		assertEquals("XBox", leiloes.get(0).getNome());
		
	}
}

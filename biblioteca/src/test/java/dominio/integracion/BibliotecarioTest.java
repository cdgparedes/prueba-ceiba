package dominio.integracion;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.LibroTestDataBuilder;

public class BibliotecarioTest {

	private static final String CRONICA_DE_UNA_MUERTA_ANUNCIADA = "Cronica de una muerta anunciada";
	private static final String USUARIO = "Cesar Dubiel";

	private SistemaDePersistencia sistemaPersistencia;

	private RepositorioLibro repositorioLibros;
	private RepositorioPrestamo repositorioPrestamo;

	@Before
	public void setUp() {

		sistemaPersistencia = new SistemaDePersistencia();

		repositorioLibros = sistemaPersistencia.obtenerRepositorioLibros();
		repositorioPrestamo = sistemaPersistencia.obtenerRepositorioPrestamos();

		sistemaPersistencia.iniciar();
	}

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void prestarLibroTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		// libro.setIsbn("ABC");
		repositorioLibros.agregar(libro);
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		// act
		blibliotecario.prestar(libro.getIsbn(), USUARIO);

		// assert
		Assert.assertTrue(blibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertNotNull(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn()));

	}

	@Test
	public void prestarLibroNoDisponibleTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();

		repositorioLibros.agregar(libro);

		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		// act
		blibliotecario.prestar(libro.getIsbn(), USUARIO);
		try {

			blibliotecario.prestar(libro.getIsbn(), USUARIO);
			fail();

		} catch (PrestamoException e) {
			// assert
			Assert.assertEquals(Bibliotecario.EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE, e.getMessage());
		}
	}

	@Test
	public void libroPalindromoTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conIsbn("1221").build();
		repositorioLibros.agregar(libro);
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		// act
		try {

			blibliotecario.prestar(libro.getIsbn(), USUARIO);
			fail();

		} catch (PrestamoException e) {
			// assert
			Assert.assertEquals(Bibliotecario.LIBRO_PALINDROMO, e.getMessage());
		}
	}

	@Test
	public void usuarioInvalidoTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conIsbn("ABC123456").build();
		repositorioLibros.agregar(libro);
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		// act
		try {

			blibliotecario.prestar(libro.getIsbn(), "  ");
			fail();

		} catch (PrestamoException e) {
			// assert
			Assert.assertEquals(Bibliotecario.USUARIO_INVALIDO, e.getMessage());
		}
	}

	@Test
	public void prestarLibroNoRegistradoTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		// act
		try {

			blibliotecario.prestar(libro.getIsbn(), USUARIO);
			fail();

		} catch (PrestamoException e) {
			// assert
			Assert.assertEquals(Bibliotecario.LIBRO_NO_REGISTRADO, e.getMessage());
		}
	}

}

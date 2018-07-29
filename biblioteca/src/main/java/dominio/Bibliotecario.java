package dominio;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.NoResultException;

import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;

public class Bibliotecario {

	public static final String EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE = "El libro no se encuentra disponible";
	public static final String LIBRO_PALINDROMO = "Los libros palindromos solo se pueden utilizar en la biblioteca";
	public static final String LIBRO_NO_REGISTRADO = "Este libro no esta registrado";
	public static final String USUARIO_INVALIDO = "El usuario no esta registrado";

	private RepositorioLibro repositorioLibro;
	private RepositorioPrestamo repositorioPrestamo;

	/**
	 * Constructor con parametros
	 * 
	 * @param repositorioLibro
	 * @param repositorioPrestamo
	 */
	public Bibliotecario(RepositorioLibro repositorioLibro, RepositorioPrestamo repositorioPrestamo) {
		this.repositorioLibro = repositorioLibro;
		this.repositorioPrestamo = repositorioPrestamo;
	}

	/**
	 * Constructor sin parametros
	 */
	public Bibliotecario() {
	}

	public boolean esPrestado(String isbn) {
		Libro libro = repositorioPrestamo.obtenerLibroPrestadoPorIsbn(isbn);

		if (libro == null) {

			return false;
		} else

			return true;
	}

	/**
	 * 
	 * @param isbn
	 */
	private void esPalidromo(String isbn) {

		int inc = 0;
		int len = isbn.length() - 1;
		boolean bError = false;

		while ((inc < len) && (!bError)) {

			if (isbn.charAt(inc) == isbn.charAt(len)) {
				inc++;
				len--;
			} else {
				bError = true;
			}
		}

		if (!bError) {
			throw new PrestamoException(LIBRO_PALINDROMO);
		}
	}

	/**
	 * 
	 * @param nombreUsuario
	 */
	private void validarUsuario(String nombreUsuario) {
		if (nombreUsuario == null)
			nombreUsuario = "";

		if (nombreUsuario.trim().length() == 0) {
			throw new PrestamoException(USUARIO_INVALIDO);
		}

	}

	/**
	 * 
	 * @param isbn
	 * @return
	 */
	private Libro validarLibro(String isbn) {

		Libro libro;

		try {
			libro = repositorioLibro.obtenerPorIsbn(isbn);

		} catch (NoResultException e) {
			throw new PrestamoException(LIBRO_NO_REGISTRADO);
		}

		esPalidromo(isbn);

		if (esPrestado(isbn)) {
			throw new PrestamoException(EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE);
		}
		return libro;
	}

	/**
	 * 
	 * @param isbn
	 * @return
	 */
	private int sumDigitos(String isbn) {

		int valor = 0;
		for (char c : isbn.toCharArray()) {
			// se valida que el cararter sea un digito
			if (Character.isDigit(c)) {
				valor = valor + Integer.parseInt(String.valueOf(c));
			}
		}
		return valor;
	}

	/**
	 * Con la función asignarFechaEntrega se calcula la fecha segùn el tamaño de
	 * del isbn.
	 * 
	 * @param isbn
	 * @return
	 */
	private Date asignarFechaEntrega(String isbn) {
		Date fechaEntregaMaxima = null;
		// Suma de digitos del isbn.
		int suma = sumDigitos(isbn);
		// Cuando la suma arroja un resultado superior a 30 , entonces se le
		// asigna la ficha maxima
		if (suma > 30) {
			Calendar calendario = Calendar.getInstance();
			calendario.add(Calendar.DAY_OF_YEAR, 16);
			// se debe adelantar un dia cuando la fecha de entrega es un
			// domingo.
			if (calendario.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				calendario.add(Calendar.DAY_OF_YEAR, 1);
			}

			fechaEntregaMaxima = calendario.getTime();
		}

		return fechaEntregaMaxima;
	}

	/**
	 *la funcion generarprestamo permite crear el prestamo teniendo en cuenta el libro y  nombreUsuario  
	 * @param libro
	 * @param nombreUsuario
	 */
	private void generarPrestamo(Libro libro, String nombreUsuario) {
		Date fechaSolicitud = new Date();
		//Se define la fecha de entrega
		Date fechaEntregaMaxima = asignarFechaEntrega(libro.getIsbn());
		//Realización del prestamo
		Prestamo prestamo = new Prestamo(fechaSolicitud, libro, fechaEntregaMaxima, nombreUsuario);
		repositorioPrestamo.agregar(prestamo);

	}

	/**
	 *La función prestar permite
	 * @param isbn
	 * @param nombreUsuario
	 */
	public void prestar(String isbn, String nombreUsuario) {
        //Se hace validacion del ingreso del nombre del usuario  
		validarUsuario(nombreUsuario);

		Libro libro = validarLibro(isbn);
		// Se crea el prestamo y se hace el cálculo de la fecha de entrega máxima 
		generarPrestamo(libro, nombreUsuario);

	}

}

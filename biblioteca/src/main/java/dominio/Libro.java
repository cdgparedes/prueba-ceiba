package dominio;

public class Libro {

	private String isbn;
	private String titulo;
	private int anio;
	private boolean estaPrestado = false;

	public Libro(String isbn, String titulo, int anio) {

		this.isbn = isbn;
		this.titulo = titulo;
		this.anio = anio;
	}

	public String getTitulo() {
		return titulo;
	}

	public int getAnio() {
		return anio;
	}

	public String getIsbn() {
		return isbn;
	}
	
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	
	public boolean getEstaPrestado() {
		return estaPrestado;
	}
	
	public void setEstaPrestado(boolean cambiarEstado) {
		this.estaPrestado = cambiarEstado;
	}

}

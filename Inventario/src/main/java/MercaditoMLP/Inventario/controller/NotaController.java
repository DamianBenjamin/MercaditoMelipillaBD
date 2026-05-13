package MercaditoMLP.Inventario.controller;

import MercaditoMLP.Inventario.model.Nota;
import MercaditoMLP.Inventario.repository.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notas")
@CrossOrigin(origins = "*")
public class NotaController {

    @Autowired
    private NotaRepository notaRepository;

    @GetMapping
    public List<Nota> listarNotas() {
        return notaRepository.findAll();
    }

    @PostMapping
    public Nota crearNota(@RequestBody Nota nota) {
        return notaRepository.save(nota);
    }

    @DeleteMapping("/{id}")
    public void eliminarNota(@PathVariable Long id) {
        notaRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Nota actualizarNota(@PathVariable Long id, @RequestBody Nota notaActualizada) {
        return notaRepository.findById(id)
                .map(nota -> {
                    if (notaActualizada.getTexto() != null) nota.setTexto(notaActualizada.getTexto());
                    if (notaActualizada.getPrioridad() != null) nota.setPrioridad(notaActualizada.getPrioridad());
                    nota.setResuelta(notaActualizada.isResuelta());
                    return notaRepository.save(nota);
                }).orElseThrow(() -> new RuntimeException("Nota no encontrada"));
    }
}
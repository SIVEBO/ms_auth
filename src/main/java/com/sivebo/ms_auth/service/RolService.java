package com.sivebo.ms_auth.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sivebo.ms_auth.dto.request.RolRequestDTO;
import com.sivebo.ms_auth.dto.response.RolResponseDTO;
import com.sivebo.ms_auth.exception.DuplicateResourceException;
import com.sivebo.ms_auth.exception.EntityNotFoundException;
import com.sivebo.ms_auth.model.Rol;
import com.sivebo.ms_auth.repository.RolRepository;
import com.sivebo.ms_auth.utils.MapToDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolService extends MapToDTO {

        private final RolRepository rolRepository;

        public List<RolResponseDTO> getAll() {
                return rolRepository.findAll()
                                .stream()
                                .map(this::mapRolToDTO)
                                .collect(Collectors.toList());
        }

        public Optional<RolResponseDTO> getById(Long id) {
                return rolRepository.findById(id).map(this::mapRolToDTO);
        }

        public RolResponseDTO create(RolRequestDTO dto) {
                if (rolRepository.existsByNombreRol(dto.getNombreRol())) {
                        throw new DuplicateResourceException("Ya existe un rol con el nombre: " + dto.getNombreRol());
                }
                Rol rol = new Rol(null, dto.getNombreRol(), dto.getDescripcion());
                log.info(">>> Rol creado: {}", dto.getNombreRol());
                return mapRolToDTO(rolRepository.save(rol));
        }

        public Optional<RolResponseDTO> update(Long id, RolRequestDTO dto) {
                return rolRepository.findById(id).map(rol -> {
                        rol.setNombreRol(dto.getNombreRol());
                        rol.setDescripcion(dto.getDescripcion());
                        log.info(">>> Rol actualizado id={}", id);
                        return mapRolToDTO(rolRepository.save(rol));
                });
        }

        public Boolean deleteById(Long id) {
                if (!rolRepository.existsById(id)) {
                        throw new EntityNotFoundException("Rol no encontrado con id: " + id);
                }
                rolRepository.deleteById(id);
                log.info(">>> Rol eliminado id={}", id);
                return true;
        }
}

package com.project.dsclient.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.dsclient.DTO.ClientDTO;
import com.project.dsclient.entities.Client;
import com.project.dsclient.repositories.ClientRepository;
import com.project.dsclient.services.exceptions.DataBaseException;
import com.project.dsclient.services.exceptions.ResourceNotFoundException;

@Service
public class ClientService {
	@Autowired
	private ClientRepository clientRepository;

	@Transactional(readOnly = true)
	public Page<ClientDTO> findAllPaged(PageRequest pageRequest) {
		Page<Client> list = clientRepository.findAll(pageRequest);
		return list.map(ClientDTO::new);
	}

	@Transactional(readOnly = true)
	public ClientDTO findById(Long id) {
		Client client = clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Id not found"));
		return new ClientDTO(client);
	}

	@Transactional
	public ClientDTO insert(ClientDTO clientDTO) {
		Client client = new Client();
		setToClient(clientDTO, client);
		return new ClientDTO(clientRepository.save(client));
	}

	private void setToClient(ClientDTO clientDTO, Client client) {
		client.setName(clientDTO.getName());
		client.setCpf(clientDTO.getCpf());
		client.setBirthDate(clientDTO.getBirthDate());
		client.setChildren(clientDTO.getChildren());
		client.setIncome(clientDTO.getIncome());
	}

	@Transactional
	public ClientDTO update(Long id, ClientDTO clientDTO) {
		try {
			Client client = clientRepository.getOne(id);
			setToClient(clientDTO, client);
			return new ClientDTO(clientRepository.save(client));
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id: " + id + " not found");
		}
	}

	@Transactional
	public void delete(Long id) {
		try {
			clientRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		} catch (Exception e) {
			throw new ResourceNotFoundException("Id: " + id + " not found");
		}
	}
}
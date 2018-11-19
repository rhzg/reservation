package de.fraunhofer.iosb.services.impl;

import de.fraunhofer.iosb.entity.Role;
import de.fraunhofer.iosb.entity.User;
import de.fraunhofer.iosb.repository.RoleRepository;
import de.fraunhofer.iosb.repository.UserRepository;
import de.fraunhofer.iosb.services.LoginService;
import de.fraunhofer.iosb.seucrity.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class LoginServiceImplementation implements LoginService
{
    @Autowired
    private UserRepository repo;

    @Autowired
    private RoleRepository roleRepo;

    public boolean checkUserNameAndPassword(String username, String password)
    {
        User user = repo.findByUsername(username);
        if(user == null)
            return false;

        BCrypt hashGenerator = new BCrypt();
        String hashedPass = hashGenerator.hashpw(password, BCrypt.gensalt());
        
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if(!passwordEncoder.matches(hashedPass, user.getPassword()))
            return false;

        return true;
    }

    @Override
    public User getUserFromToken(String authToken)
    {
        return repo.findByToken(authToken);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findOne(username);
        if(user == null)
            throw new UsernameNotFoundException(username);

        return new CustomUserDetails(user, roleRepo.findAllByUser(user));
    }

    @Override
    @Transactional
    public String createToken(User user)
    {
        if(user.getToken() != null)
        {
            return user.getToken();
        }

        String token = UUID.randomUUID().toString();

        while(repo.findByToken(token) != null)
            token = UUID.randomUUID().toString();

        user.setToken(token);
        repo.save(user);

        return token;
    }

    @Override
    public boolean checkIfAdmin(User user)
    {
        for(Role role : user.getRole())
        {
            if(role.getRole().equals("admin"))
            {
                return true;
            }
        }
        return false;
    }

}

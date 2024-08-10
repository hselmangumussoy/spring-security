package com.hsgumussoy.springsecurity.config;

import com.hsgumussoy.springsecurity.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
   private final JwtService jwtService;
   private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader("Authorization");
        final String token;
        final String username;

        if (header == null || header.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        token = header.substring(7);
        username = jwtService.findUsername(token);

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.tokenControl(token, userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                //yukarıdaki sınıf sayesinde userdetails de ki kullanıcı bilgileri alınıyor ve bu kullanıcı oluşturup yetkilendiriliyor.
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
        // Eğer kullanıcı adı null değilse ve mevcut kimlik doğrulama bilgisi yoksa
        // Kullanıcı detaylarını yükle ve token'ı doğrula
        // Eğer token geçerli ise, bir AuthenticationToken oluştur
        // Ek kimlik doğrulama detaylarını ekle
        // AuthenticationToken'ı güvenlik bağlamına yerleştir
        // İsteği ve yanıtı filtre zincirinde bir sonraki filtreye ilet

    }
}

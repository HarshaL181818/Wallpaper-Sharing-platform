package com.wallpaper.wsp.repo;

import com.wallpaper.wsp.model.Wallpaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WallpaperRepo extends JpaRepository<Wallpaper, Integer> {
    // Custom query methods can be added here if needed
}

package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {

        Artist art = null;
        for(Artist artist : artists) {
            if(artist.getName() == artistName) {
                art = artist;
                break;
            }
        }

        if(art == null) {
            Artist artist = new Artist(artistName);
            Album album = new Album(title);
            albums.add(album);

            List<Album> list = new ArrayList<>();
            list.add(album);
            artistAlbumMap.put(artist,list);
            return album;
        }

        Album album = new Album(title);
        albums.add(album);

        List<Album> list = new ArrayList<>();
        list.add(album);
        artistAlbumMap.put(art ,list);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{

        Album album = null;
        for(Album album1 : albums) {
            if(album1.getTitle() == albumName) {
                album = album1;
                break;
            }
        }

        if(album == null) {
            throw new Exception("Album does not exist");
        }
        else {
            Song song = new Song(title, length);
            songs.add(song);

            if(albumSongMap.containsKey(album)) {
                List<Song> list = albumSongMap.get(album);
                list.add(song);
                albumSongMap.put(album, list);
            }
            else {
                List<Song> list = new ArrayList<>();
                list.add(song);
                albumSongMap.put(album, list);
            }
            return song;
        }

    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = null;
        for(User user1 :users){
            if(user1.getMobile()== mobile){
                user =user1;
                break;
            }
        }
        if(user ==null){
            throw new Exception("User does not exist");
        }
        else{
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> list = new ArrayList<>();
            for(Song song :songs){
                if(song.getLength()==length){
                    list.add(song);
                }
            }
            playlistSongMap.put(playlist,list);
            List<User> list1 = new ArrayList<>();
            list1.add(user);
            playlistListenerMap.put(playlist,list1);
            creatorPlaylistMap.put(user,playlist);

            if(userPlaylistMap.containsKey(user)){
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(playlist);
                userPlaylistMap.put(user,userPlayList);
            }else{
                List<Playlist> plays = new ArrayList<>();
                plays.add(playlist);
                userPlaylistMap.put(user,plays);
            }

            return playlist;
        }

    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = null;

        for(User el : users) {
            if(el.getMobile() == mobile) {
                user = el;
                break;
            }
        }

        if(user == null) {
            throw new Exception("User does not exits");
        }
        else {
            Playlist playlist = new Playlist(title);
            playlists.add(playlist);
            List<Song> listasSameName = new ArrayList<>();

            for (Song song : songs) {
                if(songTitles.contains(song.getTitle())) {
                    listasSameName.add(song);
                }
            }
            playlistSongMap.put(playlist, listasSameName);

            List<User> list = new ArrayList<>();
            list.add(user);
            creatorPlaylistMap.put(user, playlist);

            if(userPlaylistMap.containsKey(user)) {

                List<Playlist> list1 = userPlaylistMap.get(user);

                list1.add(playlist);
                userPlaylistMap.put(user, list1);
            }
            else {
                List<Playlist> list2 = new ArrayList<>();
                list2.add(playlist);
                userPlaylistMap.put(user, list2);
            }

            return playlist;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = null;

        for(User el : users) {
            if(el.getMobile() == mobile) {
                user = el;
                break;
            }
        }

        if(user == null) {
            throw new Exception("User does not exits");
        }

//        find the playlist with given title
        Playlist playlist = null;

        for(Playlist pl : playlists) {
            if(pl.getTitle() == playlistTitle) {
                playlist = pl;
                break;
            }
        }

        if(playlist == null)
            throw new Exception("Playlist does not exist");

        if(creatorPlaylistMap.containsKey(user))
            return playlist;

        List<User> userList = playlistListenerMap.get(playlist);

        for(User user1 : userList) {
            if(user1 == user)
                return playlist;
        }
        userList.add(user);
        playlistListenerMap.put(playlist, userList);

        List<Playlist> playlists1 = userPlaylistMap.get(user);
        if(playlists1 == null)
            playlists1 = new ArrayList<>();
        playlists1.add(playlist);

        userPlaylistMap.put(user, playlists1);

        return playlist;

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;

        for(User user1 : users) {
            if(user1.getMobile() == mobile) {
                user = user1;
                break;
            }
        }

        if(user == null)
            throw new Exception("User does not  exit");

        Song song = null;

        for(Song song1 : songs) {
            if(song1.getTitle() == songTitle) {
                song = song1;break;
            }
        }

        if(song == null)
            throw new Exception("Song does not exit");

        if(songLikeMap.containsKey(song)) {
            List<User> userList = songLikeMap.get(song);
            if(userList.contains(user)) {
                return song;
            }
            else {
                song.setLikes(song.getLikes()+1);
                userList.add(user);
                songLikeMap.put(song, userList);

                Album album = null;
                for (Album album1 : albumSongMap.keySet()) {
                    List<Song> songList = albumSongMap.get(album1);

                    if(songList.contains(song)) {
                        album = album1;
                        break;
                    }
                }

                Artist artist = null;

                for (Artist artist1 : artistAlbumMap.keySet()) {
                    List<Album> albumList = artistAlbumMap.get(artist1);

                    if(albumList.contains(album)) {
                        artist = artist1;
                        break;
                    }
                }

                assert artist != null;
                artist.setLikes(artist.getLikes()+1);
                artists.add(artist);

                return song;
            }
        }
        else {
            song.setLikes(song.getLikes()+1);
            List<User> userList = new ArrayList<>();
            userList.add(user);
            songLikeMap.put(song, userList);

            Album album = null;
            for (Album album1 : albumSongMap.keySet()) {
                List<Song> songList = albumSongMap.get(album1);

                if(songList.contains(song)) {
                    album = album1;
                    break;
                }
            }

            Artist artist = null;

            for (Artist artist1 : artistAlbumMap.keySet()) {
                List<Album> albumList = artistAlbumMap.get(artist1);

                if(albumList.contains(album)) {
                    artist = artist1;
                    break;
                }
            }

            assert artist != null;
            artist.setLikes(artist.getLikes()+1);
            artists.add(artist);

            return song;
        }
    }

    public String mostPopularArtist() {
        Artist artist = null;
        int max = Integer.MIN_VALUE;
        for(Artist artist1 : artists) {
            if(artist1.getLikes() > max) {
                artist = artist1;
                max = artist1.getLikes();
            }
        }

        if(artist == null)
            return null;
        return artist.getName();
    }

    public String mostPopularSong() {
        Song song = null;
        int maxlike = Integer.MIN_VALUE;

        for(Song song1 : songs) {
            if(song1.getLikes() > maxlike) {
                song = song1;
                maxlike = song.getLikes();
            }
        }

        if(song == null)
            return null;

        return song.getTitle();

    }
}
